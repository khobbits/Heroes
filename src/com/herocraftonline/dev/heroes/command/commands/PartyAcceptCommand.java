package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyAcceptCommand extends BaseCommand {

    public PartyAcceptCommand(Heroes plugin) {
        super(plugin);
        name = "PartyAccept";
        description = "Accept a party invite";
        usage = "/party accept <player>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("party accept");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if(plugin.getServer().getPlayer(args[0]) != null) {
                Player newPlayer = plugin.getServer().getPlayer(args[0]);
                Hero newHero = plugin.getHeroManager().getHero(newPlayer);
                if(newHero.getParty().isInvited(player)) {
                    hero.setParty(newHero.getParty());
                    hero.getParty().messageParty("$1 has joined the party", player.getName());
                }else {
                    Messaging.send(player, "Sorry, $1 hasn't invited you to their party", newPlayer.getName());
                }
            }else {
                Messaging.send(player, "Sorry, $1 doesn't match anyone in-game", args[0]);
            }
        }
    }

}

