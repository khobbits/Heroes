package com.herocraftonline.dev.heroes.command.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SpecsCommand extends BasicCommand {

    private static final int SPECS_PER_PAGE = 8;
    private final Heroes plugin;

    public SpecsCommand(Heroes plugin) {
        super("Specializations");
        this.plugin = plugin;
        setDescription("Lists all specializations available to your path");
        setUsage("/hero specs §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("hero specs");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        HeroClass playerClass = plugin.getHeroManager().getHero((Player) sender).getHeroClass();

        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException ignored) {}
        }

        Set<HeroClass> childClasses = playerClass.getSpecializations();
        HeroClass[] specs = childClasses.toArray(new HeroClass[childClasses.size()]);

        if (specs.length == 0) {
            Messaging.send(sender, "$1 has no specializations.", playerClass.getName());
            return false;
        }

        int numPages = specs.length / SPECS_PER_PAGE;
        if (specs.length % SPECS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }
        sender.sendMessage("§c-----[ " + "§f" + playerClass.getName() + " Specializations <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * SPECS_PER_PAGE;
        int end = start + SPECS_PER_PAGE;
        if (end > specs.length) {
            end = specs.length;
        }
        for (int c = start; c < end; c++) {
            HeroClass heroClass = specs[c];
            String description = heroClass.getDescription();
            if (description == null || description.isEmpty()) {
                sender.sendMessage("  §a" + heroClass.getName());
            } else {
                sender.sendMessage("  §a" + heroClass.getName() + " - " + heroClass.getDescription());
            }
        }

        sender.sendMessage("§cTo choose a specialization, type §f/hero choose <spec>");
        return true;
    }

}
