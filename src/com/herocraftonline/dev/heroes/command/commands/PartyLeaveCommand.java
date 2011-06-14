package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class PartyLeaveCommand extends BaseCommand {

    public PartyLeaveCommand(Heroes plugin) {
        super(plugin);
        name = "PartyLeave";
        description = "Leave your current party";
        usage = "/party leave";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("party leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if(hero.getParty() == null) {
                return;
            }
            HeroParty heroParty = hero.getParty();
            if(hero.getParty().getLeader() == player) {
                heroParty.messageParty("$1 has disbanded the party", player.getName());
                for(Player p :hero.getParty().getMembers()) {
                    Hero tempHero = plugin.getHeroManager().getHero(p);
                    tempHero.setParty(null);
                }
                plugin.getPartyManager().removeParty(heroParty);
            }else {
                hero.setParty(null);
                heroParty.messageParty("$1 has left the party", player.getName());
            }
        }
    }

}

