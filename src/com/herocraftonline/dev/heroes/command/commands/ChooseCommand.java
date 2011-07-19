package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class ChooseCommand extends BaseCommand {

    public ChooseCommand(Heroes plugin) {
        super(plugin);
        setName("Choose Class");
        setDescription("Selects a new path or specialization");
        setUsage("/hero choose §9<type>");
        setMinArgs(1);
        setMaxArgs(1);
        getIdentifiers().add("hero choose");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        HeroClass currentClass = hero.getHeroClass();
        HeroClass newClass = plugin.getClassManager().getClass(args[0]);
        Properties prop = plugin.getConfigManager().getProperties();

        if (newClass == null) {
            Messaging.send(player, "Class not found.");
            return;
        }

        if (newClass == currentClass) {
            Messaging.send(player, "You are already set as this Class.");
            return;
        }

        if (!newClass.isPrimary()) {
            HeroClass parentClass = newClass.getParent();
            if (!hero.isMaster(parentClass)) {
                Messaging.send(player, "You must master $1 before specializing!", parentClass.getName());
                return;
            }
        }

        if (Heroes.Permissions != null && newClass != plugin.getClassManager().getDefaultClass()) {
            if (!Heroes.Permissions.has(player, "heroes.classes." + newClass.getName().toLowerCase())) {
                Messaging.send(player, "You don't have permission for $1.", newClass.getName());
                return;
            }
        }

        int cost = currentClass == plugin.getClassManager().getDefaultClass() ? 0 : prop.swapCost;

        if (prop.iConomy && this.plugin.Method != null && cost > 0) {
            if (!this.plugin.Method.getAccount(player.getName()).hasEnough(cost)) {
                // You have insufficient funds, you require $1 to change your class to the $2. -- Make the text
                // customiseable.
                Messaging.send(hero.getPlayer(), "You're unable to meet the offering of $1 to become $2.", this.plugin.Method.format(cost), newClass.getName());

                return;
            }
        }

        ClassChangeEvent event = new ClassChangeEvent(hero, currentClass, newClass);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        hero.setHeroClass(newClass);

        if (prop.resetExpOnClassChange) {
            if (!hero.isMaster(currentClass)) {
                hero.setExperience(currentClass, 0);
            }
        }

        if (prop.iConomy && this.plugin.Method != null && cost > 0) {
            if (!plugin.getConfigManager().getProperties().swapMasteryCost && !hero.isMaster(newClass)) {
                this.plugin.Method.getAccount(player.getName()).subtract(cost);
                // You have been charged $1 to swap to the $2. -- Make the text customiseable.
                Messaging.send(hero.getPlayer(), "The Gods are pleased with your offering of $1", this.plugin.Method.format(cost));
            }
        }

        hero.getBinds().clear();

        Messaging.send(player, "Welcome to the path of the $1!", newClass.getName());
    }

}
