package com.herocraftonline.dev.heroes.command.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyWhoCommand extends BaseCommand {

    public PartyWhoCommand(Heroes plugin) {
        super(plugin);
        name = "PartyWho";
        description = "Check your party members";
        usage = "/party";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("party who");
        identifiers.add("party");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.getParty() == null) {
                Messaging.send(player, "Sorry, you aren't in a party");
                return;
            }
            Messaging.send(player, "$1", partyNames(hero.getParty()).toString());
        }
    }

    public Set<String> partyNames(HeroParty party) {
        Set<String> names = new HashSet<String>();
        for (Player p : party.getMembers()) {
            names.add(p.getName());
        }
        return names;
    }

}
