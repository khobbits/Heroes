package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.common.RootEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillRoot extends TargettedSkill {

    public SkillRoot(Heroes plugin) {
        super(plugin, "Root");
        setDescription("Roots your target in place");
        setUsage("/skill root <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill root");
        setTypes(SkillType.MOVEMENT, SkillType.DEBUFF, SkillType.SILENCABLE, SkillType.EARTH, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 5000);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        long duration = getSetting(hero, Setting.DURATION.node(), 5000, false);
        RootEffect rEffect = new RootEffect(this, duration);

        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(rEffect);
        } else if (target instanceof Creature) {
            plugin.getEffectManager().addEntityEffect((Creature) target, rEffect);
        } else
            return SkillResult.INVALID_TARGET;

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }
}
