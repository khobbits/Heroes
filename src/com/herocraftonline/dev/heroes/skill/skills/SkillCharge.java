package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillCharge extends TargettedSkill {

    public SkillCharge(Heroes plugin) {
        super(plugin, "Charge");
        setDescription("Charges towards your target");
        setUsage("/skill charge");
        setArgumentRange(0, 1);
        setIdentifiers("skill charge");
        setTypes(SkillType.PHYSICAL, SkillType.MOVEMENT, SkillType.HARMFUL);
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        hero.getPlayer().teleport(target.getLocation());
        broadcastExecuteText(hero, target);
        return true;
    }
}
