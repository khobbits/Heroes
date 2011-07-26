package com.herocraftonline.dev.heroes.command.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyWhoCommand extends BasicCommand {
    private final Heroes plugin;

    public PartyWhoCommand(Heroes plugin) {
        super("Party Who");
        this.plugin = plugin;
        setDescription("Lists your party members");
        setUsage("/party who");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "party who" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        if (hero.getParty() == null) {
            Messaging.send(player, "Sorry, you aren't in a party");
            return false;
        }
        Messaging.send(player, "$1", partyNames(hero.getParty()).toString());
        return true;
    }

    public Set<String> partyNames(HeroParty party) {
        Set<String> names = new HashSet<String>();
        for (Hero partyMember : party.getMembers()) {
            names.add(partyMember.getPlayer().getName());
        }
        return names;
    }

}
