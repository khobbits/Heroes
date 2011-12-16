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

public class SkillBleed extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillBleed(Heroes plugin) {
        super(plugin, "Bleed");
        setDescription("Causes your target to bleed");
        setUsage("/skill bleed <target>");
        setArgumentRange(0, 1);
        setTypes(SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);
        setIdentifiers("skill bleed");
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 10000);
        node.set(Setting.PERIOD.node(), 2000);
        node.set("tick-damage", 1);
        node.set(Setting.APPLY_TEXT.node(), "%target% is bleeding!");
        node.set(Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT.node(), "%target% is bleeding!").replace("%target%", "$1");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!").replace("%target%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target.equals(player) || hero.getSummons().contains(target)) {
            return SkillResult.INVALID_TARGET;
        }

        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
        long period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 2000, true);
        int tickDamage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1, false);
        BleedSkillEffect bEffect = new BleedSkillEffect(this, duration, period, tickDamage, player);

        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(bEffect);
        } else 
            plugin.getEffectManager().addEntityEffect(target, bEffect);

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }

    public class BleedSkillEffect extends PeriodicDamageEffect {

        public BleedSkillEffect(Skill skill, long duration, long period, int tickDamage, Player applier) {
            super(skill, "Bleed", period, duration, tickDamage, applier);
            this.types.add(EffectType.BLEED);
        }

        @Override
        public void apply(LivingEntity lEntity) {
            super.apply(lEntity);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
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
}
