package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillBarrage extends ActiveSkill {

    public SkillBarrage(Heroes plugin) {
        super(plugin);
        name = "Barrage";
        description = "Fire a Barrage of Arrows around you.";
        usage = "/skill barrage";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill barrage");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        String playerName = player.getName();
        double diff = (2 * Math.PI) / 24.0;
        for (double a = 0; a < 2 * Math.PI; a += diff) {
            Vector vel = new Vector(Math.cos(a), 0, Math.sin(a));
            player.shootArrow().setVelocity(vel);
        }
        notifyNearbyPlayers(player.getLocation(), useText, playerName, name);
        return true;
    }
}
