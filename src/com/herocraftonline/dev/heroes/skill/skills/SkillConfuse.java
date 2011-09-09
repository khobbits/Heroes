package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
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
        setIdentifiers(new String[] { "skill confuse" });
        
        setTypes(SkillType.SILENCABLE, SkillType.ILLUSION);
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
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        Player targetPlayer = (Player) target;
        Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
        if (targetHero.equals(hero)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        broadcastExecuteText(hero, target);

        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 2000);
        float maxDrift = (float) getSetting(hero.getHeroClass(), "max-drift", 0.35);
        targetHero.addEffect(new ConfuseEffect(this, duration, period, maxDrift));
        return true;
    }

    public class ConfuseEffect extends PeriodicEffect {

        private final float maxDrift;

        public ConfuseEffect(Skill skill, long duration, long period, float maxDrift) {
            super(skill, "Confuse", period, duration);
            this.maxDrift = maxDrift;
            this.types.add(EffectType.HARMFUL);
            this.types.add(EffectType.DISPELLABLE);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);

            Player player = hero.getPlayer();
            Vector velocity = player.getVelocity();

            float angle = random.nextFloat() * 2 * 3.14159f;
            float xAdjustment = maxDrift * net.minecraft.server.MathHelper.cos(angle);
            float zAdjustment = maxDrift * net.minecraft.server.MathHelper.sin(angle);

            velocity.add(new Vector(xAdjustment, 0f, zAdjustment));
            velocity.setY(0);
            player.setVelocity(velocity);
        }
    }
}
