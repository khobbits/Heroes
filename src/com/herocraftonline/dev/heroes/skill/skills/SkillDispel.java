package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillDispel extends TargettedSkill {

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
        if (!(target instanceof Player)) {
            return false;
        }

        Player targetPlayer = (Player) target;
        Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
        for (String s : targetHero.getEffects().getEffects()) {
            targetHero.getEffects().removeEffect(s);
        }

        notifyNearbyPlayers(player.getLocation(), getUseText(), player.getName(), name, getEntityName(target));
        return true;
    }

}
