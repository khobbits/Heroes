package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class AdminClassCommand extends BaseCommand {

    public AdminClassCommand(Heroes plugin) {
        super(plugin);
        name = "AdminClassCommand";
        description = "Changes a users class";
        usage = "/hero admin class §9<player> <class>";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("hero admin class");
        this.permissionNode = "heroes.admin.classchange";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = plugin.getServer().getPlayer(args[0]);
        HeroClass heroClass = plugin.getClassManager().getClass(args[1]);
        // Check the Player exists.
        if (player == null) {
            Messaging.send(sender, "Failed to find a matching Player for '$1'.", args[0]);
            return;
        }
        // Check the HeroClass exists.
        if (heroClass == null) {
            Messaging.send(sender, "Failed to find a matching HeroClass for '$1'.", args[1]);
            return;
        }
        // Check the Player is not the same HeroClass as we are trying to assign.
        Hero hero = plugin.getHeroManager().getHero(player);
        if (hero.getHeroClass().equals(heroClass)) {
            Messaging.send(sender, "$1 is already a $2.", player.getName(), heroClass.getName());
            return;
        }
        // Change the Players HeroClass and reset their Bindings.
        hero.changeHeroClass(heroClass);
        // Alert both the Admin and the Player of the change.
        Messaging.send(sender, "You have successfully changed $1 HeroClass to $2.", player.getName(), heroClass.getName());
        Messaging.send(player, "Welcome to the path of the $1!", heroClass.getName());
    }
}
