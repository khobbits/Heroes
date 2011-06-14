package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class PartyCreateCommand extends BaseCommand {

    public PartyCreateCommand(Heroes plugin) {
        super(plugin);
        name = "PartyCreate";
        description = "Creates a party";
        usage = "/party create";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("party create");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if(hero.getParty() != null) {
                HeroParty newParty = new HeroParty(player);
                newParty.addMember(player);
                plugin.getPartyManager().addParty(newParty);
                hero.setParty(newParty);
            }
        }
    }

}

