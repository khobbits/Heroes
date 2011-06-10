package com.herocraftonline.dev.heroes.command.skill.skills;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class SkillWolf extends ActiveSkill {

    public SkillWolf(Heroes plugin) {
        super(plugin);
        name = "Wolf";
        description = "Summons and tames a wolf to your side";
        usage = "/skill wolf";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill wolf");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        LivingEntity le = player.getWorld().spawnCreature(hero.getPlayer().getLocation(), CreatureType.WOLF);
        Wolf wolf = (Wolf) le;
        wolf.setOwner(player);
        wolf.setTamed(true);
        return true;
    }
}
