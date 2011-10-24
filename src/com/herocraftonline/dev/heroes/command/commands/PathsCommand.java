package com.herocraftonline.dev.heroes.command.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.command.CommandHandler;

public class PathsCommand extends BasicCommand {

    private static final int PATHS_PER_PAGE = 8;
    private final Heroes plugin;

    public PathsCommand(Heroes plugin) {
        super("Paths");
        this.plugin = plugin;
        setDescription("Lists all paths available to you");
        setUsage("/hero paths §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("hero paths");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException ignored) {}
        }

        Set<HeroClass> classes = plugin.getClassManager().getClasses();
        Set<HeroClass> primaryClasses = new HashSet<HeroClass>();
        for (HeroClass heroClass : classes) {
            if (heroClass.hasNoParents()) {
                primaryClasses.add(heroClass);
            }
        }
        HeroClass[] paths = primaryClasses.toArray(new HeroClass[primaryClasses.size()]);

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

            if (!heroClass.isDefault() && !CommandHandler.hasPermission(sender, "heroes.classes." + heroClass.getName().toLowerCase())) {
                continue;
            }

            String description = heroClass.getDescription();
            if (description == null || description.isEmpty()) {
                sender.sendMessage("  §a" + heroClass.getName());
            } else {
                sender.sendMessage("  §a" + heroClass.getName() + " - " + heroClass.getDescription());
            }
        }

        sender.sendMessage("§cTo choose a path, type §f/hero choose <path>");
        return true;
    }

}
