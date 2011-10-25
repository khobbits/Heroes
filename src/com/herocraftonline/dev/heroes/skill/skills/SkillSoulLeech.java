package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSoulLeech extends TargettedSkill {

    private String expireText;

    public SkillSoulLeech(Heroes plugin) {
        super(plugin, "SoulLeech");
        setDescription("You slowly drain the life out of the player.");
        setUsage("/skill soulleech <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill soulleech", "skill sleech");
        setTypes(SkillType.HEAL, SkillType.DAMAGING, SkillType.SILENCABLE, SkillType.DARK, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000); // in milliseconds
        node.setProperty(Setting.PERIOD.node(), 2000); // in milliseconds
        node.setProperty("tick-damage", 1);
        node.setProperty("heal-multiplier", 1);
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% is no longer draining %target%'s soul!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% looks healthier from draining %target%'s soul!").replace("%hero%", "$1").replace("%target%", "$2");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        long duration = getSetting(hero, Setting.DURATION.node(), 10000, false);
        long period = getSetting(hero, Setting.PERIOD.node(), 2000, true);
        int tickDamage = getSetting(hero, "tick-damage", 1, false);

        SoulLeechEffect slEffect = new SoulLeechEffect(this, period, duration, tickDamage, player);

        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(slEffect);
        } else if (target instanceof Creature) {
            Creature creature = (Creature) target;
            plugin.getEffectManager().addCreatureEffect(creature, slEffect);
        } else {
            Messaging.send(player, "Invalid target!");
            return false;
        }

        broadcastExecuteText(hero, target);
        return true;
    }

    public class SoulLeechEffect extends PeriodicDamageEffect {

        private int totalDamage = 0;

        public SoulLeechEffect(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "SoulLeech", period, duration, tickDamage, applier);
            this.types.add(EffectType.HARMFUL);
            this.types.add(EffectType.DARK);
            this.types.add(EffectType.DISPELLABLE);
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            healApplier();
            broadcast(creature.getLocation(), expireText, applier.getDisplayName(), Messaging.getCreatureName(creature).toLowerCase());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            healApplier();
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, applier.getDisplayName(), player.getDisplayName());
        }

        @Override
        public void tick(Creature creature) {
            super.tick(creature);
            totalDamage += tickDamage;
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);
            totalDamage += tickDamage;
        }

        private void healApplier() {
            Hero hero = plugin.getHeroManager().getHero(applier);
            int healAmount = totalDamage * getSetting(hero, "heal-multiplier", 1, false);

            // Fire our heal event
            HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, healAmount, skill);
            plugin.getServer().getPluginManager().callEvent(hrhEvent);
            if (hrhEvent.isCancelled())
                return;

            hero.setHealth(hero.getHealth() + hrhEvent.getAmount());
            hero.syncHealth();
        }
    }
}
