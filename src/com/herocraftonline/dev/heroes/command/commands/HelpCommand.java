/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dev.heroes.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.command.Command;
import com.herocraftonline.dev.heroes.command.CommandHandler;

public class HelpCommand extends BasicCommand {

    private static final int CMDS_PER_PAGE = 8;
    private final Heroes plugin;

    public HelpCommand(Heroes plugin) {
        super("Help");
        this.plugin = plugin;
        setDescription("Displays the help menu");
        setUsage("/hero help §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("hero", "hero help");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {}
        }

        List<Command> sortCommands = plugin.getCommandHandler().getCommands();
        List<Command> commands = new ArrayList<Command>();

        // Filter out Skills from the command list.
        for (Command command : sortCommands) {
            if (command.isShownOnHelpMenu()) {
                if (CommandHandler.hasPermission(sender, command.getPermission())) {
                    commands.add(command);
                }
            }
        }

        int numPages = commands.size() / CMDS_PER_PAGE;
        if (commands.size() % CMDS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage("§c-----[ " + "§fHeroes Help <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        if (end > commands.size()) {
            end = commands.size();
        }
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);
            sender.sendMessage("  §a" + cmd.getUsage());
        }

        sender.sendMessage("§cFor more info on a particular command, type §f/<command> ?");
        return true;
    }

}
