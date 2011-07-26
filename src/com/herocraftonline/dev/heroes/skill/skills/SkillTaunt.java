package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillTaunt extends ActiveSkill {

    public SkillTaunt(Heroes plugin) {
        super(plugin, "Taunt");
        setDescription("Taunts enemies around you");
        setUsage("/skill taunt");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill taunt" });
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        List<Entity> entities = hero.getPlayer().getNearbyEntities(5, 5, 5);
        for (Entity n : entities) {
            if (n instanceof Monster) {
                ((Monster) n).setTarget(hero.getPlayer());
            }
        }
        broadcastExecuteText(hero);
        return true;
    }

}
