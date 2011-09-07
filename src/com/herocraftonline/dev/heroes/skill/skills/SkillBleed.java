package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBleed extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillBleed(Heroes plugin) {
        super(plugin, "Bleed");
        setDescription("Causes your target to bleed");
        setUsage("/skill bleed <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill bleed" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        node.setProperty(Setting.PERIOD.node(), 2000);
        node.setProperty("tick-damage", 1);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is bleeding!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is bleeding!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!").replace("%target%", "$1");
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
        
        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 2000);
        int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 1);
        BleedSkillEffect bEffect = new BleedSkillEffect(this, duration, period, tickDamage, player);

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

    public class BleedSkillEffect extends PeriodicDamageEffect {

        public BleedSkillEffect(Skill skill, long duration, long period, int tickDamage, Player applier) {
            super(skill, "Bleed", period, duration, tickDamage, applier);
            this.types.add(EffectType.BLEED);
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
    }
}
