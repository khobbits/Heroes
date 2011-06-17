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
            if (plugin.getServer().getPlayer(args[0]) != null) {
                Player newPlayer = plugin.getServer().getPlayer(args[0]);
                Hero newHero = plugin.getHeroManager().getHero(newPlayer);
                if (hero.getParty() != null) {
                    Messaging.send(player, "Sorry, you're already in a party", (String[]) null);
                    return;
                }
                if (newHero.getParty() != null && newHero.getParty().isInvited(player.getName())) {
                    hero.setParty(newHero.getParty());
                    newHero.getParty().addMember(hero.getPlayer());
                    hero.getParty().messageParty("$1 has joined the party", player.getName());
                    Messaging.send(player, "You're now in $1's party", newPlayer.getName());
                } else {
                    Messaging.send(player, "Sorry, $1 hasn't invited you to their party", newPlayer.getName());
                }
            } else {
                Messaging.send(player, "Sorry, $1 doesn't match anyone in-game", args[0]);
            }
        }
    }

}
