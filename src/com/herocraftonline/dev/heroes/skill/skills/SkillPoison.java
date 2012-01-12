package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPoison extends TargettedSkill {

    private String expireText;

    public SkillPoison(Heroes plugin) {
        super(plugin, "Poison");
        setDescription("You poison your target dealing $1 damage over $2 seconds.");
        setUsage("/skill poison <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill poison");
        setTypes(SkillType.DAMAGING, SkillType.SILENCABLE, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 10000); // in milliseconds
        node.set(Setting.PERIOD.node(), 2000); // in milliseconds
        node.set("tick-damage", 1);
        node.set(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
        long period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 2000, true);
        int tickDamage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1, false);
        PoisonSkillEffect pEffect = new PoisonSkillEffect(this, period, duration, tickDamage, player);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(pEffect);
        } else 
            plugin.getEffectManager().addEntityEffect(target, pEffect);

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }

    public class PoisonSkillEffect extends PeriodicDamageEffect {
        
        public PoisonSkillEffect(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "Poison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
            addMobEffect(19, (int) (duration / 1000) * 20, 0, true);
        }

        @Override
        public void apply(LivingEntity lEntity) {
            super.apply(lEntity);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
        }

        @Override
        public void remove(LivingEntity lEntity) {
            super.remove(lEntity);
            broadcast(lEntity.getLocation(), expireText, Messaging.getLivingEntityName(lEntity).toLowerCase());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }
    }

    @Override
    public String getDescription(Hero hero) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
        double period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 2000, false);
        int damage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1, false);
        return getDescription().replace("$1", damage * duration / period + "").replace("$2", duration / 1000 + "");
    }
}
