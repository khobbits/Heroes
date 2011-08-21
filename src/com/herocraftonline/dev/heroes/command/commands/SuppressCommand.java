package com.herocraftonline.dev.heroes.command.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.command.Command;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SuppressCommand extends BasicCommand {
    private final Heroes plugin;

    public SuppressCommand(Heroes plugin) {
        super("Suppress");
        this.plugin = plugin;
        setDescription("Toggles the suppression of skill messages");
        setUsage("/hero stfu §9<skill>");
        setArgumentRange(1, 1);
        setIdentifiers(new String[] { "hero stfu", "hero suppress" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);

        if (args.length == 0) {
            Set<String> suppressions = hero.getSuppressedSkills();
            if (suppressions.isEmpty()) {
                Messaging.send(player, "No skills suppressed.");
                return false;
            }

            String list = "Suppressing ";
            for (String skill : suppressions) {
                list += skill + ", ";
            }
            list = list.substring(0, list.length() - 2);

            Messaging.send(player, list);
        } else {
            Command cmd = plugin.getCommandHandler().getCommand(args[0]);
            if (cmd == null || !(cmd instanceof Skill)) {
                Messaging.send(player, "Skill not found.");
                return false;
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

        return true;
    }
}
