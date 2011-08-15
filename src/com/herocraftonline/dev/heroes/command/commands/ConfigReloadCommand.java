package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.util.Messaging;

public class ConfigReloadCommand extends BasicCommand {

    private final Heroes plugin;

    public ConfigReloadCommand(Heroes plugin) {
        super("Reload");
        this.plugin = plugin;
        setDescription("Reloads the heroes config file");
        setUsage("/hero admin reload");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"hero admin reload"});
        setPermission("heroes.admin.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        if (!Heroes.Permissions.has((Player) sender, "heroes.admin.reload")) {
            Messaging.send(sender, "Insufficient permission.");
            return false;
        }

        if (plugin.getConfigManager().reload()) {
            Messaging.send(sender, "Configs reloaded.");
        }

        return true;
    }

}
