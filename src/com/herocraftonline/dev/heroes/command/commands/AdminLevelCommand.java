package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class AdminLevelCommand extends BasicCommand {

    private final Heroes plugin;

    public AdminLevelCommand(Heroes plugin) {
        super("AdminExpCommand");
        this.plugin = plugin;
        setDescription("Changes a users level");
        setUsage("/hero admin level §9<player> <level>");
        setArgumentRange(2, 2);
        setIdentifiers("hero admin level");
        setPermission("heroes.admin.level");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Player player = plugin.getServer().getPlayer(args[0]);
        // Check the Player exists.
        if (player == null) {
            Messaging.send(sender, "Failed to find a matching Player for '$1'.  Offline players are not supported!", args[0]);
            return false;
        }
        Hero hero = plugin.getHeroManager().getHero(player);

        try {
            int levelChange = Integer.parseInt(args[1]);
            if (levelChange < 1)
                throw new NumberFormatException();

            int experience = Properties.levels[levelChange - 1];
            hero.gainExp(experience - hero.getExperience(), ExperienceType.ADMIN, false);
            Messaging.send(sender, "Level changed.");
            return true;
        } catch (NumberFormatException e) {
            Messaging.send(sender, "Invalid level value.");
            return false;
        }

    }
}
