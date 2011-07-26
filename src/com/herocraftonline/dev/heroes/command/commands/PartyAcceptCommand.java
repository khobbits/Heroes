package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyAcceptCommand extends BasicCommand {
    private final Heroes plugin;

    public PartyAcceptCommand(Heroes plugin) {
        super("Party Accept");
        this.plugin = plugin;
        setDescription("Accept a party invite");
        setUsage("/party accept <player>");
        setArgumentRange(1, 1);
        setIdentifiers(new String[] { "party accept" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        if (plugin.getServer().getPlayer(args[0]) != null) {
            Player newPlayer = plugin.getServer().getPlayer(args[0]);
            Hero newHero = plugin.getHeroManager().getHero(newPlayer);
            if (hero.getParty() != null) {
                Messaging.send(player, "Sorry, you're already in a party");
                return false;
            }
            if (newHero.getParty() != null && newHero.getParty().isInvited(player.getName())) {
                hero.setParty(newHero.getParty());
                newHero.getParty().addMember(hero);
                hero.getParty().messageParty("$1 has joined the party", player.getName());
                Messaging.send(player, "You're now in $1's party", newPlayer.getName());
                hero.getParty().removeInvite(player);
            } else {
                Messaging.send(player, "Sorry, $1 hasn't invited you to their party", newPlayer.getName());
            }
        } else {
            Messaging.send(player, "Sorry, $1 doesn't match anyone in-game", args[0]);
        }

        return true;
    }

}
