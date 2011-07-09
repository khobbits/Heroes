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
        setName("Party Chat");
        setDescription("Sends messages to your party");
        setUsage("/party <msg> OR /p <msg>");
        setMinArgs(1);
        setMaxArgs(1000);
        getIdentifiers().add("pc");
        getIdentifiers().add("p");
        getIdentifiers().add("party");
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

            Set<Hero> partyMembers = party.getMembers();
            if (partyMembers.size() <= 1) {
                Messaging.send(player, "Your party is empty.");
                return;
            }

            String msg = "";
            for (String word : args) {
                msg += word + " ";
            }
            msg = msg.trim();

            if (player.equals(party.getLeader())) {
                msg = "\u00a7a[Party] \u00a7e" + player.getDisplayName() + "\u00a7a:\u00a73 " + msg;
            } else {
                msg = "\u00a7a[Party] \u00a77" + player.getDisplayName() + "\u00a7a:\u00a73 " + msg;
            }

            for (Hero partyMember : partyMembers) {
                partyMember.getPlayer().sendMessage(msg);
            }
        }
    }

}
