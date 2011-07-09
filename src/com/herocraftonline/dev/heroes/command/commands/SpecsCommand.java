package com.herocraftonline.dev.heroes.command.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SpecsCommand extends BaseCommand {
    private static final int SPECS_PER_PAGE = 8;

    public SpecsCommand(Heroes plugin) {
        super(plugin);
        setName("Specializations");
        setDescription("Lists all specializations available to your path");
        setUsage("/hero specs [page#]");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("hero specs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        HeroClass playerClass = plugin.getHeroManager().getHero((Player) sender).getHeroClass();

        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {}
        }

        Set<HeroClass> childClasses = playerClass.getSpecializations();
        HeroClass[] specs = childClasses.toArray(new HeroClass[0]);

        if (specs.length == 0) {
            Messaging.send(sender, "$1 has no specializations.", playerClass.getName());
            return;
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

        sender.sendMessage("§cTo choose a specialization, type '/hero choose <spec>'");
    }

}
