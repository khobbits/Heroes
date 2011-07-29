package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashMap;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillWolf extends ActiveSkill {

    public HashMap<Player, Integer> wolves = new HashMap<Player, Integer>();

    public SkillWolf(Heroes plugin) {
        super(plugin, "Wolf");
        setDescription("Summons and tames a wolf to your side");
        setUsage("/skill wolf");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill wolf"});
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        if (wolves.containsKey(player) == false || wolves.get(player) <= 3) {
            LivingEntity le = player.getWorld().spawnCreature(hero.getPlayer().getLocation(), CreatureType.WOLF);
            Wolf wolf = (Wolf) le;
            wolf.setOwner(player);
            wolf.setTamed(true);
            int wolf1 = wolves.containsKey(player) ? wolves.get(player) + 1 : 1;
            wolves.remove(player);
            wolves.put(player, wolf1);
            return true;
        } else {
            Messaging.send(player, "Sorry, you have too many wolves already");
            return false;
        }
    }
}
