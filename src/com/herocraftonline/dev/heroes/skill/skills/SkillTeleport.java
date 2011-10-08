package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillTeleport extends ActiveSkill {

    public SkillTeleport(Heroes plugin) {
        super(plugin, "Teleport");
        setDescription("Teleports you (roughly) to your party member!");
        setUsage("/skill teleport <player>");
        setArgumentRange(1, 1);
        setIdentifiers("skill teleport");
        setTypes(SkillType.TELEPORT, SkillType.SILENCABLE);
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        if (!(hero.getParty() != null && hero.getParty().getMembers().size() > 0)) {
            Messaging.send(player, "Sorry, you need to be in a party with players!");
            return false;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            Messaging.send(player, "Sorry, that player doesn't exist!");
            return false;
        }

        if (!hero.getParty().isPartyMember(plugin.getHeroManager().getHero(targetPlayer))) {
            Messaging.send(player, "Sorry, that player isn't in your party!");
            return false;
        }

        Location loc1 = targetPlayer.getLocation().add(Util.rand.nextDouble() * (-50 + hero.getLevel() - (50 - hero.getLevel())), 0, Util.rand.nextDouble() * (-50 + hero.getLevel() - (50 - hero.getLevel())));
        Double highestBlock = (double) targetPlayer.getWorld().getHighestBlockYAt(loc1);
        loc1.setY(highestBlock);
        player.teleport(loc1);

        broadcastExecuteText(hero);
        return true;
    }
}
