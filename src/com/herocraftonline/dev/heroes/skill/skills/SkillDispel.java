package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillDispel extends TargettedSkill{

    public SkillDispel(Heroes plugin) {
        super(plugin);
        name = "Dispel";
        description = "Removes all effects from your target";
        usage = "/skill dispel";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("skill dispel"); 
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if(!(target instanceof Player)) {
            Messaging.send(player, "Invalid Target", (String[])null);
            return false;
        }
        Player targetPlayer = (Player) target;
        Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
        for(String s : targetHero.getEffects().getEffects()) {
            targetHero.getEffects().removeEffect(s);
        }
        notifyNearbyPlayers(hero.getPlayer().getLocation(), useText, hero.getPlayer().getName(), name, getEntityName(target));
        return true;
    }

}
