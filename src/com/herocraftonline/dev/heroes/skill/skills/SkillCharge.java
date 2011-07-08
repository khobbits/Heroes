package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillCharge extends TargettedSkill {

    public SkillCharge(Heroes plugin) {
        super(plugin);
        name = "Charge";
        description = "Charges towards your target";
        usage = "/skill charge";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("skill charge");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if(target instanceof Player) {
            Player p = (Player) target;
            if(p == hero.getPlayer()) {
                return false;
            }
        }
        hero.getPlayer().teleport(target.getLocation());
        notifyNearbyPlayers(hero.getPlayer().getLocation(), getUseText(), hero.getPlayer().getName(), name);
        return true;
    }

}
