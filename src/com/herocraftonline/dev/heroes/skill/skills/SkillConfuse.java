package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillConfuse extends TargettedSkill {

    private static final Random random = new Random();

    private String applyText;
    private String expireText;

    public SkillConfuse(Heroes plugin) {
        super(plugin, "Confuse");
        setDescription("Confuses your target");
        setUsage("/skill confuse <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill confuse");
        setTypes(SkillType.SILENCABLE, SkillType.ILLUSION, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        node.setProperty(Setting.PERIOD.node(), 1000);
        node.setProperty("max-drift", 0.35);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is confused!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has regained his wit!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is confused!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has regained his wit!").replace("%target%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        long duration = getSetting(hero, Setting.DURATION.node(), 10000, false);
        long period = getSetting(hero, Setting.PERIOD.node(), 2000, true);
        float maxDrift = (float) getSetting(hero, "max-drift", 0.35, false);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(new ConfuseEffect(this, duration, period, maxDrift));
        } else if (target instanceof Creature) {
            plugin.getEffectManager().addCreatureEffect((Creature) target, new ConfuseEffect(this, duration, period, maxDrift));
        } else
            return SkillResult.INVALID_TARGET;

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }

    public class ConfuseEffect extends PeriodicExpirableEffect {

        private final float maxDrift;

        public ConfuseEffect(Skill skill, long duration, long period, float maxDrift) {
            super(skill, "Confuse", period, duration);
            this.maxDrift = maxDrift;
            this.types.add(EffectType.HARMFUL);
            this.types.add(EffectType.DISPELLABLE);
            addMobEffect(9, (int) (duration / 1000) * 20, 127, false);
        }

        public void adjustVelocity(LivingEntity lEntity) {
            Vector velocity = lEntity.getVelocity();

            float angle = random.nextFloat() * 2 * 3.14159f;
            float xAdjustment = maxDrift * net.minecraft.server.MathHelper.cos(angle);
            float zAdjustment = maxDrift * net.minecraft.server.MathHelper.sin(angle);

            velocity.add(new Vector(xAdjustment, 0f, zAdjustment));
            velocity.setY(0);
            lEntity.setVelocity(velocity);
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature));
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void tick(Creature creature) {
            super.tick(creature);
            adjustVelocity(creature);
            creature.setTarget(null);
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);
            adjustVelocity(hero.getPlayer());
        }
    }
}
