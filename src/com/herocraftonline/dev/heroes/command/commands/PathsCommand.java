package com.herocraftonline.dev.heroes.command.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;

public class PathsCommand extends BaseCommand {
    private static final int PATHS_PER_PAGE = 8;

    public PathsCommand(Heroes plugin) {
        super(plugin);
        name = "Paths";
        description = "Lists all paths available to you";
        usage = "/hero paths [page#]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("hero paths");
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

        Set<HeroClass> classes = plugin.getClassManager().getClasses();
        Set<HeroClass> primaryClasses = new HashSet<HeroClass>();
        for (HeroClass heroClass : classes) {
            if (heroClass.isPrimary()) {
                primaryClasses.add(heroClass);
            }
        }
        HeroClass[] paths = primaryClasses.toArray(new HeroClass[0]);

        int numPages = paths.length / PATHS_PER_PAGE;
        if (paths.length % PATHS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage("§c-----[ " + "§fHeroes Paths <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * PATHS_PER_PAGE;
        int end = start + PATHS_PER_PAGE;
        if (end > paths.length) {
            end = paths.length;
        }
        for (int c = start; c < end; c++) {
            HeroClass heroClass = paths[c];
            String description = heroClass.getDescription();
            if (description == null || description.isEmpty()) {
                sender.sendMessage("  §a" + heroClass.getName());
            } else {
                sender.sendMessage("  §a" + heroClass.getName() + " - " + heroClass.getDescription());
            }
        }

        sender.sendMessage("§cTo choose a path, type '/hero choose <path>'");
    }

}
