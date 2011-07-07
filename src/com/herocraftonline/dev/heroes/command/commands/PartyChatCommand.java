package com.herocraftonline.dev.heroes.command.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyChatCommand extends BaseCommand {

    public PartyChatCommand(Heroes plugin) {
        super(plugin);
        name = "PartyChat";
        description = "Sends messages to your party";
        usage = "/party <msg> OR /p <msg>";
        minArgs = 1;
        maxArgs = 1000000;
        identifiers.add("pc");
        identifiers.add("p");
        identifiers.add("party");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);

            HeroParty party = hero.getParty();
            if (party == null) {
                Messaging.send(player, "You are not in a party.");
                return;
            }

            Set<Player> partyMembers = party.getMembers();
            if (partyMembers.size() <= 1) {
                Messaging.send(player, "Your party is empty.");
                return;
            }

            String msg = "";
            for (String word : args) {
                msg += word + " ";
            }
            msg = msg.trim();

            msg = "\u00a7b[Party] \u00a7f" + player.getDisplayName() + "\u00a7b: " + msg;

            for (Player partyMember : partyMembers) {
                partyMember.sendMessage(msg);
            }
        }
    }

}
