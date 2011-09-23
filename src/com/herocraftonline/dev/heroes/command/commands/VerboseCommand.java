package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class VerboseCommand extends BasicCommand {
    private final Heroes plugin;

    public VerboseCommand(Heroes plugin) {
        super("Verbose");
        this.plugin = plugin;
        setDescription("Toggles display of mana and exp gains");
        setUsage("/hero verbose");
        setArgumentRange(0, 0);
        setIdentifiers("hero verbose");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        boolean verbose = hero.isVerbose();
        hero.setVerbose(!verbose);
        if (hero.isVerbose()) {
            Messaging.send(player, "Now displaying mana and exp gains.");
        } else {
            Messaging.send(player, "No longer displaying mana and exp gains.");
        }

        return true;
    }

}
