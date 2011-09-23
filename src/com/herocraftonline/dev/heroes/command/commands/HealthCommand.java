package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HealthCommand extends BasicCommand {

    private final Heroes plugin;

    public HealthCommand(Heroes plugin) {
        super("Health");
        this.plugin = plugin;
        setDescription("Displays your health");
        setUsage("/hp");
        setArgumentRange(0, 0);
        setIdentifiers("health", "hp");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        double hp = hero.getHealth();
        double maxHp = hero.getMaxHealth();

        player.sendMessage(Messaging.createFullHealthBar(hp, maxHp));
        return true;
    }
}
