package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.command.CommandHandler;
import com.herocraftonline.dev.heroes.hero.Hero;
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
        if (!CommandHandler.hasPermission(sender, "heroes.admin.reload")) {
            Messaging.send(sender, "Insufficient permission.");
            return false;
        }

        for (Hero hero : plugin.getHeroManager().getHeroes()) {
            plugin.getHeroManager().saveHero(hero);
        }
        Messaging.send(sender, "You have saved all loaded Heroes.");
        return true;
    }
}
