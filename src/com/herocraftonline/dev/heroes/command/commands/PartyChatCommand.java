package com.herocraftonline.dev.heroes.command.commands;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class PartyChatCommand extends BasicCommand {
    private final Heroes plugin;

    public PartyChatCommand(Heroes plugin) {
        super("Party Chat");
        this.plugin = plugin;
        setDescription("Sends messages to your party");
        setUsage("/party §9<msg> OR /p §9<msg>");
        setArgumentRange(1, 1000);
        setIdentifiers("pc", "p", "party");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);

        HeroParty party = hero.getParty();
        if (party == null) {
            Messaging.send(player, "You are not in a party.");
            return false;
        }

        Set<Hero> partyMembers = party.getMembers();
        if (partyMembers.size() <= 1) {
            Messaging.send(player, "Your party is empty.");
            return false;
        }

        StringBuilder msg = new StringBuilder();
        for (String word : args) {
            msg.append(word).append(' ');
        }
        String fullMsg = msg.toString().trim();

        if (player.equals(party.getLeader())) {
            fullMsg = "\u00a7a[Party] \u00a7e" + player.getDisplayName() + "\u00a7a:\u00a73 " + fullMsg;
        } else {
            fullMsg = "\u00a7a[Party] \u00a77" + player.getDisplayName() + "\u00a7a:\u00a73 " + fullMsg;
        }

        for (Hero partyMember : partyMembers) {
            partyMember.getPlayer().sendMessage(fullMsg);
        }

        return true;
    }
}
