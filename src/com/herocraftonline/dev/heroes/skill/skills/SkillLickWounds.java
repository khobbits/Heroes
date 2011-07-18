package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillLickWounds extends ActiveSkill {

    public SkillLickWounds(Heroes plugin) {
        super(plugin);
        setName("LickWounds");
        setDescription("Heals your nearby wolves");
        setUsage("/skill lickwounds");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill lickwounds");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        List<Entity> entityList = player.getNearbyEntities(10, 10, 10);
        for (Entity n : entityList) {
            if (n instanceof Wolf) {
                Wolf nWolf = (Wolf) n;
                if (nWolf.getOwner() == player) {
                    int hpPlus = 30;
                    if (nWolf.getHealth() + hpPlus > 200) {
                        hpPlus = 200 - nWolf.getHealth();
                    }
                    nWolf.setHealth(nWolf.getHealth() + hpPlus);
                }
            }
        }
        broadcastExecuteText(hero);
        return true;
    }

}
