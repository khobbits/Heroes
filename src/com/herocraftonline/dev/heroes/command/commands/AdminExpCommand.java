package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class AdminExpCommand extends BaseCommand {

    public AdminExpCommand(Heroes plugin) {
        super(plugin);
        name = "AdminExpCommand";
        description = "Changes a users exp";
        usage = "/hero admin class §9<player> <exp>";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("hero admin exp");
        this.permissionNode = "heroes.admin.exp";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = plugin.getServer().getPlayer(args[0]);
        Hero hero = plugin.getHeroManager().getHero(player);
        // Check the Player exists.
        if (player == null) {
            Messaging.send(sender, "Failed to find a matching Player for '$1'.", args[0]);
            return;
        }
        hero.setExperience(Integer.parseInt(args[1]));
    }
}
