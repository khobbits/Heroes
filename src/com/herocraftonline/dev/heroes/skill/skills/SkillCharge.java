package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillCharge extends TargettedSkill {

    public SkillCharge(Heroes plugin) {
        super(plugin, "Charge");
        setDescription("Charges towards your target");
        setUsage("/skill charge");
        setArgumentRange(0, 1);
        setIdentifiers(new String[]{"skill charge"});
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (target instanceof Player) {
            Player p = (Player) target;
            if (p == hero.getPlayer())
                return false;
        }
        hero.getPlayer().teleport(target.getLocation());
        broadcastExecuteText(hero, target);
        return true;
    }

}
