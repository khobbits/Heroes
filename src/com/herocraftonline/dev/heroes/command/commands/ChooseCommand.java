package com.herocraftonline.dev.heroes.command.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicInteractiveCommand;
import com.herocraftonline.dev.heroes.command.BasicInteractiveCommandState;
import com.herocraftonline.dev.heroes.command.InteractiveCommandState;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class ChooseCommand extends BasicInteractiveCommand {

    private final Heroes plugin;
    private Map<Player, HeroClass> pendingClassSelections = new HashMap<Player, HeroClass>();

    public ChooseCommand(Heroes plugin) {
        super("Choose Class");
        this.plugin = plugin;
        this.setStates(new InteractiveCommandState[] { new StateA(), new StateB() });
        setDescription("Selects a new path or specialization");
        setUsage("/hero choose §9<type>");
    }

    @Override
    public String getCancelIdentifier() {
        return "cancel";
    }

    class StateA extends BasicInteractiveCommandState {

        public StateA() {
            super("hero choose");
            this.setArgumentRange(1, 1);
        }

        @Override
        public boolean execute(CommandSender executor, String identifier, String[] args) {
            if (!(executor instanceof Player))
                return false;

            Player player = (Player) executor;
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
            boolean costApplied = false;
            if (prop.iConomy && plugin.Method != null && cost > 0) {
                if (!hero.isMaster(newClass) || prop.swapMasteryCost) {
                    costApplied = true;
                    if (!plugin.Method.getAccount(player.getName()).hasEnough(cost)) {
                        Messaging.send(hero.getPlayer(), "You're unable to meet the offering of $1 to become $2.", plugin.Method.format(cost), newClass.getName());
                        return false;
                    }
                }
            }

            Messaging.send(executor, "You have chosen...");
            Messaging.send(executor, "$1: $2", newClass.getName(), newClass.getDescription().toLowerCase());
            String skills = newClass.getSkillNames().toString();
            skills = skills.substring(1, skills.length() - 1);
            Messaging.send(executor, "$1: $2", "Skills", skills);
            if (costApplied) {
                Messaging.send(executor, "$1: $2", "Fee", plugin.Method.format(cost));
            }
            Messaging.send(executor, "Please §8/confirm §7 or §8/cancel §7this selection.");

            pendingClassSelections.put(player, newClass);
            return true;
        }

    }

    class StateB extends BasicInteractiveCommandState {

        public StateB() {
            super("confirm");
            this.setArgumentRange(0, 0);
        }

        @Override
        public boolean execute(CommandSender executor, String identifier, String[] args) {
            if (!(executor instanceof Player))
                return false;

            Player player = (Player) executor;
            Hero hero = plugin.getHeroManager().getHero(player);
            HeroClass currentClass = hero.getHeroClass();
            HeroClass newClass = pendingClassSelections.get(player);
            Properties prop = plugin.getConfigManager().getProperties();

            ClassChangeEvent event = new ClassChangeEvent(hero, currentClass, newClass);
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return false;

            hero.clearEffects(); // clear any leftover/passive effects
            hero.setHeroClass(newClass);

            if (prop.resetExpOnClassChange) {
                if (!hero.isMaster(currentClass)) {
                    hero.setExperience(currentClass, 0);
                }
            }

            int cost = currentClass == plugin.getClassManager().getDefaultClass() ? 0 : prop.swapCost;

            if (prop.iConomy && plugin.Method != null && cost > 0) {
                if (!hero.isMaster(newClass) || prop.swapMasteryCost) {
                    plugin.Method.getAccount(player.getName()).subtract(cost);
                    Messaging.send(hero.getPlayer(), "The Gods are pleased with your offering of $1.", plugin.Method.format(cost));
                }
            }
            
            //Cleanup stuff
            plugin.getHeroManager().performSkillChecks(hero);
            hero.getBinds().clear();

            Messaging.send(player, "Welcome to the path of the $1!", newClass.getName());
            return true;
        }

    }

    @Override
    public void onCommandCancelled(CommandSender executor) {
        if (!(executor instanceof Player))
            return;
        pendingClassSelections.remove((Player) executor);
    }

}
