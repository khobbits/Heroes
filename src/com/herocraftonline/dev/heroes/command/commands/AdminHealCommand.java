package com.herocraftonline.dev.heroes.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class AdminHealCommand extends BasicCommand {

    private final Heroes plugin;

    public AdminHealCommand(Heroes plugin) {
        super("AdminHeal");
        this.plugin = plugin;
        setDescription("Heals a hero to full health");
        setUsage("/hero admin heal §9<player>");
        setArgumentRange(0, 3);
        setIdentifiers("hero admin heal");
        setPermission("heroes.admin.heal");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (args.length > 0) {
            List<Player> players = plugin.getServer().matchPlayer(args[0]);
            if (players.isEmpty()) {
                Messaging.send(sender, "No player named $1 was found!", args[0]);
                return true;
            } 
            String names = "";
            for (Player player : players) {
                Hero hero = plugin.getHeroManager().getHero(player);
                hero.setHealth(hero.getMaxHealth());
                hero.syncHealth();
                Messaging.send(player, "The gods have miraculously healed you!");
                names += player.getDisplayName() + "  ";
            }
            Messaging.send(sender, "You have restored: $1to full health.", names);
            return true;
        } else if (sender instanceof ConsoleCommandSender) {
            Messaging.send(sender, "You must specify a player to heal.");
            return true;
        } else {
            Hero hero = plugin.getHeroManager().getHero((Player) sender);
            hero.setHealth(hero.getMaxHealth());
            hero.syncHealth();
            Messaging.send(sender, "You have been restored to full health!");
            return true;
        }
    }

}
