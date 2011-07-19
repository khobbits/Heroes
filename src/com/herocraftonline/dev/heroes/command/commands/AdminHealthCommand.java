package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class AdminHealthCommand extends BaseCommand {

    public AdminHealthCommand(Heroes plugin) {
        super(plugin);
        setName("AdminHealthCommand");
        setDescription("Sets a user's health");
        setUsage("/hero admin hp §9<player> <health>");
        setMinArgs(2);
        setMaxArgs(2);
        getIdentifiers().add("hero admin hp");
        setPermissionNode("heroes.admin.healthchange");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = plugin.getServer().getPlayer(args[0]);
        // Check the Player exists.
        if (player == null) {
            Messaging.send(sender, "Failed to find a matching Player for $1.", args[0]);
            return;
        }
        // Check if the health is valid.
        double health;
        try {
            health = Double.valueOf(args[1]);
        } catch (NumberFormatException e) {
            Messaging.send(sender, "Invalid health.");
            return;
        }
        // Change the player's health
        Hero hero = plugin.getHeroManager().getHero(player);
        hero.setHealth(health);
        hero.syncHealth();
        Messaging.send(sender, "You have successfully changed $1's health.", player.getName());
    }
}
