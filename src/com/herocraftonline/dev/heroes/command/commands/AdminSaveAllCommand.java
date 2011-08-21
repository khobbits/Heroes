package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class AdminSaveAllCommand extends BasicCommand {

    private final Heroes plugin;

    public AdminSaveAllCommand(Heroes plugin) {
        super("AdminSaveAllCommand");
        this.plugin = plugin;
        setDescription("Saves all heros online");
        setUsage("/hero admin saveall");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "hero admin saveall" });
        setPermission("heroes.admin.saveall");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (sender instanceof Player) {
            if (!Heroes.Permissions.has((Player) sender, "heroes.admin.reload")) {
                Messaging.send(sender, "Insufficient permission.");
                return false;
            }
        }

        for (Hero hero : plugin.getHeroManager().getHeroes()) {
            plugin.getHeroManager().saveHero(hero.getPlayer());
        }
        Messaging.send(sender, "You have saved all loaded Heroes.");
        return true;
    }
}
