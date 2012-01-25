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
import com.herocraftonline.dev.heroes.command.CommandHandler;
import com.herocraftonline.dev.heroes.command.InteractiveCommandState;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class ChooseCommand extends BasicInteractiveCommand {

    private final Heroes plugin;
    private Map<String, HeroClass> pendingClassSelections = new HashMap<String, HeroClass>();
    private Map<String, Double> pendingClassCostStatus = new HashMap<String, Double>();

    public ChooseCommand(Heroes plugin) {
        super("Choose Class");
        this.plugin = plugin;
        this.setStates(new InteractiveCommandState[] { new StateA(), new StateB() });
        setDescription("Selects a path or specialization");
        setUsage("/hero choose §9<type>");
    }

    @Override
    public String getCancelIdentifier() {
        return "hero cancel";
    }

    @Override
    public void onCommandCancelled(CommandSender executor) {
        if (!(executor instanceof Player)) {
            return;
        }
        pendingClassSelections.remove(executor);
        pendingClassCostStatus.remove(executor);
    }

    class StateA extends BasicInteractiveCommandState {

        public StateA() {
            super("hero choose");
            this.setArgumentRange(1, 1);
        }

        @Override
        public boolean execute(CommandSender executor, String identifier, String[] args) {
            if (!(executor instanceof Player)) {
                return false;
            }

            Properties props = Heroes.properties;
            Player player = (Player) executor;
            Hero hero = plugin.getHeroManager().getHero(player);
            HeroClass currentClass = hero.getHeroClass();
            HeroClass newClass = plugin.getClassManager().getClass(args[0]);

            if (newClass == null) {
                Messaging.send(player, "Class not found.");
                return false;
            }

            if (newClass.equals(currentClass) || newClass.equals(hero.getSecondClass())) {
                Messaging.send(player, "You are already set as this Class.");
                return false;
            }

            if (!hero.getHeroClass().isDefault() && hero.isMaster(currentClass) && currentClass.getParents().isEmpty() && props.lockAtHighestTier) {
                Messaging.send(player, "You have mastered your class and can not choose a new one!");
                return false;
            }

            if (!newClass.isPrimary()) {
                Messaging.send(player, "That is not a primary Class!");
                return false;
            }

            if (!hero.isMaster(currentClass) && props.lockPathTillMaster) {
                Messaging.send(player, "You must master this class before swapping to another.");
                return false;
            }

            if (!newClass.hasNoParents()) {
                for (HeroClass parentClass : newClass.getStrongParents()) {
                    if (!hero.isMaster(parentClass)) {
                        Messaging.send(player, "$1 requires you to master: $2", newClass.getName(), newClass.getStrongParents().toString().replace("[", "").replace("]", ""));
                        return false;
                    }
                }
                boolean masteredOne = false;
                for (HeroClass parentClass : newClass.getWeakParents()) {
                    if (hero.isMaster(parentClass)) {
                        masteredOne = true;
                        break;
                    }
                }

                if (!masteredOne && !newClass.getWeakParents().isEmpty()) {
                    Messaging.send(player, "$1 requires you to master one of: $2", newClass.getName(), newClass.getWeakParents().toString().replace("[", "").replace("]", ""));
                    return false;
                }
            }

            if (!newClass.isDefault() && !CommandHandler.hasPermission(player, "heroes.classes." + newClass.getName().toLowerCase())) {
                Messaging.send(player, "You don't have permission for $1.", newClass.getName());
                return false;
            }

            double cost = newClass.getCost();
            if (hero.getExperience(newClass) > 0) {
                cost = props.oldClassSwapCost;
            }

            if (props.firstSwitchFree && currentClass.isDefault()) {
                cost = 0;
            } else if (hero.isMaster(newClass) && props.swapMasterFree) {
                cost = 0;
            } else if (!props.economy || Heroes.econ == null || cost <= 0) {
                cost = 0;
            } else if (props.economy && cost > 0 && !Heroes.econ.has(player.getName(), cost)) {
                Messaging.send(player, "It will cost $1 to switch classes, you only have $2", Heroes.econ.format(cost), Heroes.econ.format(Heroes.econ.getBalance(player.getName())));
                return false;
            }
            pendingClassCostStatus.put(player.getName(), cost);

            Messaging.send(executor, "You have chosen...");
            Messaging.send(executor, "$1: $2", newClass.getName(), newClass.getDescription().toLowerCase());
            String skills = newClass.getSkillNames().toString();
            skills = skills.substring(1, skills.length() - 1);
            Messaging.send(executor, "$1: $2", "Skills", skills);
            if (cost > 0) {
                Messaging.send(executor, "$1: $2", "Fee", Heroes.econ.format(cost));
            }
            if (props.resetProfOnPrimaryChange) {
                Messaging.send(executor, "Switching your primary class will also reset all experience toward your profession!");
            }
            Messaging.send(executor, "Please §8/hero confirm §7 or §8/hero cancel §7this selection.");

            pendingClassSelections.put(player.getName(), newClass);
            return true;
        }

    }

    class StateB extends BasicInteractiveCommandState {

        public StateB() {
            super("hero confirm");
            this.setArgumentRange(0, 0);
        }

        @Override
        public boolean execute(CommandSender executor, String identifier, String[] args) {
            if (!(executor instanceof Player)) {
                return false;
            }

            Player player = (Player) executor;
            Hero hero = plugin.getHeroManager().getHero(player);
            HeroClass currentClass = hero.getHeroClass();
            HeroClass newClass = pendingClassSelections.remove(player.getName());
            Properties prop = Heroes.properties;

            double cost = pendingClassCostStatus.remove(player.getName());

            if ( cost > 0) {
                if (Heroes.econ.has(player.getName(), cost)) {
                    Heroes.econ.withdrawPlayer(player.getName(), cost);
                    Messaging.send(hero.getPlayer(), "The Gods are pleased with your offering of $1.", Heroes.econ.format(cost));
                } else {
                    Messaging.send(hero.getPlayer(), "You're unable to meet the offering of $1 to become $2.", Heroes.econ.format(cost), newClass.getName());
                    return false;
                }
            }

            ClassChangeEvent event = new ClassChangeEvent(hero, currentClass, newClass);
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            if (prop.resetExpOnClassChange || prop.resetMasteryOnClassChange) {
                if (!hero.isMaster(currentClass) || (prop.resetMasteryOnClassChange)) {
                    hero.setExperience(currentClass, 0);
                }
            }

            hero.changeHeroClass(newClass, false);
            Messaging.send(player, "Welcome to the path of the $1!", newClass.getName());
            if (prop.resetProfOnPrimaryChange && hero.getSecondClass() != null) {
                hero.setExperience(hero.getSecondClass(), 0);
            }
            plugin.getHeroManager().saveHero(hero);
            return true;
        }
    }
}
