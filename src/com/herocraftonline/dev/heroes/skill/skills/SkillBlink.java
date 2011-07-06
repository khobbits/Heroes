package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillBlink extends ActiveSkill {

    public SkillBlink(Heroes plugin) {
        super(plugin);
        name = "Blink";
        description = "Teleports you 4-5 blocks";
        usage = "/skill blink";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill blink");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        List<Block> blocks = hero.getPlayer().getLineOfSight(null, 6);
        if (blocks.get(blocks.size() - 1).getType() != Material.AIR) {

        }
        float yaw = hero.getPlayer().getLocation().getYaw();
        hero.getPlayer().teleport(blocks.get(blocks.size() - 1).getLocation());
        hero.getPlayer().getLocation().setYaw(yaw);
        return true;
    }

}
