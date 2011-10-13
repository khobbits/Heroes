package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;

public class DebugDumpCommand extends BasicCommand {

    public DebugDumpCommand() {
        super("Debug Dump");
        setDescription("Displays debug information");
        setUsage("/hero debug dump");
        setArgumentRange(0, 0);
        setPermission("heroes.admin.debug");
        setIdentifiers("hero debug dump");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        sender.sendMessage(Heroes.debug.dump());
        return true;
    }

}
