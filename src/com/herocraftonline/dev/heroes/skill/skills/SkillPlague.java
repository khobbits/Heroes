package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.DiseaseEffect;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPlague extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillPlague(Heroes plugin) {
        super(plugin, "Plague");
        setDescription("You infect your target with the plague!");
        setUsage("/skill plague <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill plague" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 21000);
        node.setProperty(Setting.PERIOD.node(), 3000);
        node.setProperty("tick-damage", 1);
        node.setProperty(Setting.RADIUS.node(), 4);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is infected with the plague!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% is no longer infected with the plague!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is infected with the plague!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% is no longer infected with the plague!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target.equals(player) || hero.getSummons().contains(target)) {
            Messaging.send(player, "You need a target!");
            return false;
        }
        
        // PvP test
        Hero targetHero = null;
        if (target instanceof Player) {
            EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
            plugin.getServer().getPluginManager().callEvent(damageEntityEvent);
            if (damageEntityEvent.isCancelled()) {
                Messaging.send(player, "Invalid target!");
                return false;
            }
            targetHero = plugin.getHeroManager().getHero((Player) target);
        }
        
        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 21000);
        long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 3000);
        int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 1);
        PlagueEffect bEffect = new PlagueEffect(this, duration, period, tickDamage, player);

        if (targetHero != null) {
            targetHero.addEffect(bEffect);
        } else if (target instanceof Creature) {
            Creature creature = (Creature) target;
            plugin.getHeroManager().addCreatureEffect(creature, bEffect);
        } else {
            Messaging.send(player, "Invalid target!");
            return false;
        }
        
        broadcastExecuteText(hero, target);
        return true;
    }

    public class PlagueEffect extends DiseaseEffect implements Dispellable {

        public PlagueEffect(Skill skill, long duration, long period, int tickDamage, Player applier) {
            super(skill, "Plague", period, duration, tickDamage, applier);
        }
        
        //Clone Constructor
        private PlagueEffect(PlagueEffect pEffect) {
            super(pEffect.skill, pEffect.name, pEffect.getPeriod(), pEffect.getDuration(), pEffect.tickDamage, pEffect.applier);
        }
        
        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);

            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }
        
        @Override
        public void tick(Creature creature) {
            super.tick(creature);
            spreadToNearbyEntities(creature);
        }
        
        @Override
        public void tick(Hero hero) {
            super.tick(hero);
            spreadToNearbyEntities(hero.getPlayer());
        }
        
        /**
         * Attempts to spread the effect to all nearby entities
         * Will not target non-pvpable targets
         * 
         * @param lEntity
         */
        private void spreadToNearbyEntities(LivingEntity lEntity) {
            int radius = getSetting(applyHero.getHeroClass(), Setting.RADIUS.node(), 4);
            for (Entity target : lEntity.getNearbyEntities(radius, radius, radius)) {
                if (!(target instanceof LivingEntity) || target.equals(applier) || applyHero.getSummons().contains(target)) {
                    continue;
                }
                //PvP Check
                if (target instanceof Player) {
                    EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(applier, target, DamageCause.CUSTOM, 0);
                    plugin.getServer().getPluginManager().callEvent(damageEntityEvent);
                    if (damageEntityEvent.isCancelled()) {
                        continue;
                    }
                    
                    Hero tHero = plugin.getHeroManager().getHero((Player) target);
                    //Ignore heroes that already have the plague effect
                    if (tHero.hasEffect("Plague"))
                        continue;
                    
                    //Apply the effect to the hero creating a copy of the effect
                    tHero.addEffect(new PlagueEffect(this));
                } else if (target instanceof Creature) {
                    Creature creature = (Creature) target;
                    //Make sure the creature doesn't already have the effect
                    if (plugin.getHeroManager().creatureHasEffect(creature, "Plague"))
                        continue;
                    
                    //Apply the effect to the creature, creating a copy of the effect
                    plugin.getHeroManager().addCreatureEffect(creature, new PlagueEffect(this));
                }
            }
        }
    }
}