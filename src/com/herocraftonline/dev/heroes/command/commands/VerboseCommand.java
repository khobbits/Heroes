package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class VerboseCommand extends BaseCommand {

    public VerboseCommand(Heroes plugin) {
        super(plugin);
        setName("Verbose");
        setDescription("Toggles display of mana and exp gains");
        setUsage("/hero verbose");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("hero verbose");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            boolean verbose = hero.isVerbose();
            verbose = !verbose;
            hero.setVerbose(verbose);
            if (verbose) {
                Messaging.send(player, "Now displaying mana and exp gains.");
            } else {
                Messaging.send(player, "No longer displaying mana and exp gains.");
            }
        }
    }

}
