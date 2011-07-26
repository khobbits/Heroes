package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class ChooseCommand extends BasicCommand {

    private final Heroes plugin;

    public ChooseCommand(Heroes plugin) {
        super("Choose Class");
        this.plugin = plugin;
        setDescription("Selects a new path or specialization");
        setUsage("/hero choose §9<type>");
        setArgumentRange(1, 1);
        setIdentifiers(new String[] { "hero choose" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        HeroClass currentClass = hero.getHeroClass();
        HeroClass newClass = plugin.getClassManager().getClass(args[0]);
        Properties prop = plugin.getConfigManager().getProperties();

        if (newClass == null) {
            Messaging.send(player, "Class not found.");
            return false;
        }

        if (newClass == currentClass) {
            Messaging.send(player, "You are already set as this Class.");
            return false;
        }

        if (!newClass.isPrimary()) {
            HeroClass parentClass = newClass.getParent();
            if (!hero.isMaster(parentClass)) {
                Messaging.send(player, "You must master $1 before specializing!", parentClass.getName());
                return false;
            }
        }

        if (Heroes.Permissions != null && newClass != plugin.getClassManager().getDefaultClass()) {
            if (!Heroes.Permissions.has(player, "heroes.classes." + newClass.getName().toLowerCase())) {
                Messaging.send(player, "You don't have permission for $1.", newClass.getName());
                return false;
            }
        }

        int cost = currentClass == plugin.getClassManager().getDefaultClass() ? 0 : prop.swapCost;

        if (prop.iConomy && this.plugin.Method != null && cost > 0) {
            if (!plugin.getConfigManager().getProperties().swapMasteryCost && !hero.isMaster(newClass)) {
                if (!this.plugin.Method.getAccount(player.getName()).hasEnough(cost)) {
                    // You have insufficient funds, you require $1 to change your class to the $2. -- Make the text
                    // customiseable.
                    Messaging.send(hero.getPlayer(), "You're unable to meet the offering of $1 to become $2.", this.plugin.Method.format(cost), newClass.getName());

                    return false;
                }
            }
        }

        ClassChangeEvent event = new ClassChangeEvent(hero, currentClass, newClass);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

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
        return true;
    }

}
