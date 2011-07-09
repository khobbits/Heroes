package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyInviteCommand extends BaseCommand {

    private static final int MAX_PARTY_SIZE = 10;

    public PartyInviteCommand(Heroes plugin) {
        super(plugin);
        setName("Party Invite");
        setDescription("Invites a player to your party");
        setUsage("/party invite");
        setMinArgs(1);
        setMaxArgs(1);
        getIdentifiers().add("party invite");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.getParty() == null) {
                HeroParty newParty = new HeroParty(hero);
                plugin.getPartyManager().addParty(newParty);
                hero.setParty(newParty);
                Messaging.send(player, "Your party has been created");
            }

            HeroParty party = hero.getParty();

            Player target = plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                Messaging.send(player, "Player not found.");
                return;
            }

            if (!party.getLeader().equals(hero)) {
                Messaging.send(player, "You are not leader of this party.");
                return;
            }

            if (target.equals(player)) {
                Messaging.send(player, "You cannot invite yourself.");
                return;
            }

            int memberCount = party.getMembers().size();

            if (memberCount >= MAX_PARTY_SIZE) {
                Messaging.send(player, "Your party is full.");
                return;
            }

            if (memberCount + party.getInviteCount() >= MAX_PARTY_SIZE) {
                party.removeOldestInvite();
            }

            party.addInvite(target.getName());
            Messaging.send(target, "$1 has invited you to their party", player.getName());
            Messaging.send(target, "Type /party accept $1 to join", player.getName());
            Messaging.send(player, "$1 has been invited to your party", target.getName());
        }
    }

}
