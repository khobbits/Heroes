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
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.command.skill.Skill;

public class HelpCommand extends BaseCommand {
    private static final int CMDS_PER_PAGE = 8;

    public HelpCommand(Heroes plugin) {
        super(plugin);
        name = "Help";
        description = "Displays the help menu";
        usage = "/hero help [page#]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("hero");
        identifiers.add("hero help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
            }
        }

        List<BaseCommand> sortCommands = plugin.getCommandManager().getCommands();
        List<BaseCommand> commands = new ArrayList<BaseCommand>();

        // Filter out Skills from the command list.
        for (BaseCommand command : sortCommands) {
            if (!(command instanceof Skill)) {
                commands.add(command);
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
            BaseCommand cmd = commands.get(c);
            sender.sendMessage("  §a" + cmd.getUsage());
        }

        sender.sendMessage("§cFor more info on a particular command, type '/<command> ?'");
    }

}
