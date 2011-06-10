package com.herocraftonline.dev.heroes.command.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class SkillTaunt extends ActiveSkill {

    public SkillTaunt(Heroes plugin) {
        super(plugin);
        name = "Taunt";
        description = "Taunts enemies around you";
        usage = "/skill taunt";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill taunt");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        List<Entity> entities = hero.getPlayer().getNearbyEntities(5, 5, 5);
        for (Entity n : entities) {
            if (n instanceof Monster) {
                ((Monster) n).setTarget(hero.getPlayer());
            }
        }
        return true;
    }

}
