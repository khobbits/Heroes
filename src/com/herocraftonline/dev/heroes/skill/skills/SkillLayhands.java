package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillLayhands extends TargettedSkill {

    public SkillLayhands(Heroes plugin) {
        super(plugin);
        name = "Layhands";
        description = "Heals the target to full";
        usage = "/skill layhands [target]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("skill layhands");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        target.setHealth(20);
        notifyNearbyPlayers(player.getLocation(), useText, player.getName(), name, target == player ? "himself" : getEntityName(target));
        return true;
    }
}
