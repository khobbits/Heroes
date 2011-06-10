package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;

public class AdminClassCommand extends BaseCommand {

    public AdminClassCommand(Heroes plugin) {
        super(plugin);
        name = "AdminClassCommand";
        description = "Changes a users class";
        usage = "/hero admin class §9<player> <class>";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("hero admin change");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Heroes.Permissions.has((Player) sender, "heroes.admin.reload")) {
            return;
        }
        if(plugin.getServer().getPlayer(args[0]) != null && plugin.getClassManager().getClass(args[1]) != null){
            plugin.getHeroManager().getHero(plugin.getServer().getPlayer(args[0])).setHeroClass(plugin.getClassManager().getClass(args[1]));
        }
    }
}
