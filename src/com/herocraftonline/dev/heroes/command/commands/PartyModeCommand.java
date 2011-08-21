package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyModeCommand extends BasicCommand {
    private final Heroes plugin;

    public PartyModeCommand(Heroes plugin) {
        super("Party Mode");
        this.plugin = plugin;
        setDescription("Toggles exp sharing or party pvp");
        setUsage("/party mode §9<pvp|exp>");
        setArgumentRange(1, 1);
        setIdentifiers(new String[] { "party mode" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);

        if (hero.getParty() == null) {
            Messaging.send(player, "You are not in a party.");
            return false;
        }

        HeroParty heroParty = hero.getParty();
        if (heroParty.getLeader().equals(hero)) {
            if (args[0].equalsIgnoreCase("pvp")) {
                heroParty.pvpToggle();
            } else if (args[0].equalsIgnoreCase("exp")) {
                heroParty.expToggle();
            }
            return true;
        } else {
            Messaging.send(player, "Sorry, you need to be the leader to do that");
            return false;
        }
    }

}
