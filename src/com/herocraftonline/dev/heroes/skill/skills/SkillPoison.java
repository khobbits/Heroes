package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
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

public class SkillPoison extends TargettedSkill {

    private String expireText;

    public SkillPoison(Heroes plugin) {
        super(plugin, "Poison");
        setDescription("Poisons your target");
        setUsage("/skill poison <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill poison");

        setTypes(SkillType.DAMAGING, SkillType.SILENCABLE, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000); // in milliseconds
        node.setProperty(Setting.PERIOD.node(), 2000); // in milliseconds
        node.setProperty("tick-damage", 1);
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 2000);
        int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 1);
        PoisonSkillEffect pEffect = new PoisonSkillEffect(this, period, duration, tickDamage, player);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(pEffect);
        } else if (target instanceof Creature) {
            Creature creature = (Creature) target;
            plugin.getHeroManager().addCreatureEffect(creature, pEffect);
        } else {
            Messaging.send(player, "Invalid target!");
            return false;
        }

        broadcastExecuteText(hero, target);
        return true;
    }

    public class PoisonSkillEffect extends PeriodicDamageEffect {

        private MobEffect mobEffect = new MobEffect(19, 0, 0);
        
        public PoisonSkillEffect(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "Poison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
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
    }
}
