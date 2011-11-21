package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillLayhands extends TargettedSkill {

    public SkillLayhands(Heroes plugin) {
        super(plugin, "Layhands");
        setDescription("Heals the target to full");
        setUsage("/skill layhands <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill layhands");
        setTypes(SkillType.LIGHT, SkillType.HEAL, SkillType.SILENCABLE);
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        if (!(target instanceof Player)) {
            return SkillResult.INVALID_TARGET;
        }

        Hero targetHero = plugin.getHeroManager().getHero((Player) target);

        targetHero.setHealth(targetHero.getMaxHealth());
        targetHero.syncHealth();

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }
}
