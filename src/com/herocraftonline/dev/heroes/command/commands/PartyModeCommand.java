package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyModeCommand extends BaseCommand {

    public PartyModeCommand(Heroes plugin) {
        super(plugin);
        name = "PartyMode";
        description = "Change your parties mode";
        usage = "/party mode <pvp>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("party mode");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.getParty() == null) {
                return;
            }
            HeroParty heroParty = hero.getParty();
            if (heroParty.getLeader() == player) {
                if(heroParty.checkMode(args[0])){
                    heroParty.removeMode(args[0]);
                }else {
                    heroParty.addMode(args[0]);
                }
            } else {
                Messaging.send(player, "Sorry, you need to be the leader to do that");

            }
        }
    }

}
