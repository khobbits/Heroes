package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyInviteCommand extends BaseCommand {

    public PartyInviteCommand(Heroes plugin) {
        super(plugin);
        name = "PartyInvite";
        description = "Invite a player to a party, must be party leader";
        usage = "/party invite";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("party invite");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if(hero.getParty() != null && hero.getParty().getLeader() == player && plugin.getServer().getPlayer(args[0]) != null) {
                hero.getParty().addInvite(plugin.getServer().getPlayer(args[0]));
                Messaging.send(plugin.getServer().getPlayer(args[0]), "$1 has invited you to their party", player.getName());
            }
        }
    }

}

