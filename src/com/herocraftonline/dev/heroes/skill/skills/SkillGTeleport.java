package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillGTeleport extends ActiveSkill {

    public SkillGTeleport(Heroes plugin) {
        super(plugin);
        name = "GroupTeleport";
        description = "Summons your group to your location";
        usage = "/skill groupteleport";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill groupteleport");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        if (hero.getParty() != null && hero.getParty().getMembers().size() != 1) {
            Player player = hero.getPlayer();
            String heroName = player.getName();
            for (Player n : hero.getParty().getMembers()) {
                n.teleport(player);
            }
            notifyNearbyPlayers(player.getLocation(), useText, heroName, name);
            return true;
        }
        return false;
    }
}
