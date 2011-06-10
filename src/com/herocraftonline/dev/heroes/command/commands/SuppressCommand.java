package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.command.skill.Skill;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SuppressCommand extends BaseCommand {

    public SuppressCommand(Heroes plugin) {
        super(plugin);
        name = "Suppress";
        description = "Toggles the suppression of skill messages";
        usage = "/hero stfu [skill]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("hero stfu");
        identifiers.add("hero suppress");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);

            if (args.length == 0) {
                String[] suppressions = hero.getSuppressedSkills();
                if (suppressions.length == 0) {
                    Messaging.send(player, "No skills suppressed.");
                    return;
                }

                String list = "Suppressing ";
                for (String skill : suppressions) {
                    list += skill + ", ";
                }
                list = list.substring(0, list.length() - 2);

                Messaging.send(player, list);
            } else {
                BaseCommand cmd = plugin.getCommandManager().getCommand(args[0]);
                if (cmd == null || !(cmd instanceof Skill)) {
                    Messaging.send(player, "Skill not found.");
                    return;
                }
                Skill skill = (Skill) cmd;
                if (hero.isSuppressing(skill)) {
                    hero.setSuppressed(skill, false);
                    Messaging.send(player, "Messages from $1 are no longer suppressed.", skill.getName());
                } else {
                    hero.setSuppressed(skill, true);
                    Messaging.send(player, "Messages from $1 are now suppressed.", skill.getName());
                }
            }
        }
    }
}
