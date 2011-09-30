package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillRevive extends ActiveSkill {

    public SkillRevive(Heroes plugin) {
        super(plugin, "Revive");
        setDescription("Teleports the target to their place of death");
        setUsage("/skill revive <target>");
        setArgumentRange(1, 1);
        setIdentifiers("skill revive");
        setTypes(SkillType.HEAL, SkillType.SILENCABLE);
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("Player not found.");
            return false;
        }

        Player targetPlayer = target;
        String targetName = targetPlayer.getName();
        if (!Util.deaths.containsKey(targetName)) {
            Messaging.send(player, "$1 has not died recently.", targetName);
            return false;
        }

        Location deathLoc = Util.deaths.get(targetName);
        Location playerLoc = player.getLocation();
        if (!playerLoc.getWorld().equals(deathLoc.getWorld()) || playerLoc.distance(deathLoc) > 50.0) {
            Messaging.send(player, "You are out of range.");
            return false;
        }

        if (targetPlayer.isDead()) {
            Messaging.send(player, "$1 is still dead.", targetName);
            return false;
        }

        Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
        if (!hero.hasParty() || !hero.getParty().isPartyMember(targetHero)) {
            Messaging.send(player, "The person needs to be in your party to do that!");
            return false;
        }

        targetPlayer.teleport(playerLoc);

        broadcastExecuteText(hero);
        return true;
    }
}
