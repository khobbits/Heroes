package com.herocraftonline.dev.heroes.command.commands;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealthCommand extends BasicCommand {

    private final Heroes plugin;

    public HealthCommand(Heroes plugin) {
        super("Health");
        this.plugin = plugin;
        setDescription("Displays your health");
        setUsage("/hp");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"health", "hp"});
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        double hp = hero.getHealth();
        double maxHp = hero.getMaxHealth();
        
        player.sendMessage(Messaging.createFullHealthBar(hp, maxHp));
        return true;
    }
}
