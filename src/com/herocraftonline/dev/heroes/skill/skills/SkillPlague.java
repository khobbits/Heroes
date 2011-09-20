package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
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
        setIdentifiers("skill plague");
        setTypes(SkillType.DARK, SkillType.DAMAGING, SkillType.SILENCABLE, SkillType.HARMFUL);
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

        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 21000);
        long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 3000);
        int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 1);
        PlagueEffect bEffect = new PlagueEffect(this, duration, period, tickDamage, player);

        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(bEffect);
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

    public class PlagueEffect extends PeriodicDamageEffect {

        private MobEffect mobEffect = new MobEffect(19, 0, 0);
        
        public PlagueEffect(Skill skill, long duration, long period, int tickDamage, Player applier) {
            super(skill, "Plague", period, duration, tickDamage, applier);
            this.mobEffect = new MobEffect(19, (int) (duration / 1000) * 20, 0);
        }

        // Clone Constructor
        private PlagueEffect(PlagueEffect pEffect) {
            super(pEffect.getSkill(), pEffect.getName(), pEffect.getPeriod(), pEffect.getDuration(), pEffect.tickDamage, pEffect.applier);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.DISEASE);
            this.mobEffect = new MobEffect(19, (int) (pEffect.getDuration() / 1000) * 20, 0);
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.netServerHandler.sendPacket(new Packet41MobEffect(entityPlayer.id, this.mobEffect));
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);

            Player player = hero.getPlayer();
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.netServerHandler.sendPacket(new Packet42RemoveMobEffect(entityPlayer.id, this.mobEffect));
            broadcast(player.getLocation(), expireText, player.getDisplayName());
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

                if (!damageCheck(getApplier(), (LivingEntity) target)) {
                    continue;
                }

                if (target instanceof Player) {
                    Hero tHero = plugin.getHeroManager().getHero((Player) target);
                    // Ignore heroes that already have the plague effect
                    if (tHero.hasEffect("Plague")) {
                        continue;
                    }

                    // Apply the effect to the hero creating a copy of the effect
                    tHero.addEffect(new PlagueEffect(this));
                } else if (target instanceof Creature) {
                    Creature creature = (Creature) target;
                    // Make sure the creature doesn't already have the effect
                    if (plugin.getHeroManager().creatureHasEffect(creature, "Plague")) {
                        continue;
                    }

                    // Apply the effect to the creature, creating a copy of the effect
                    plugin.getHeroManager().addCreatureEffect(creature, new PlagueEffect(this));
                }
            }
        }
    }
}
