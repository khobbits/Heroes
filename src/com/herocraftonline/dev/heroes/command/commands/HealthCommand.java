package com.herocraftonline.dev.heroes.command.commands;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealthCommand extends BaseCommand {

    public HealthCommand(Heroes plugin) {
        super(plugin);
        setName("Health");
        setDescription("Displays your health");
        setUsage("/hp");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("health");
        getIdentifiers().add("hp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            double hp = hero.getHealth();
            double maxHp = hero.getMaxHealth();
            Messaging.send(player, "Health: $1/$2", (int) hp, (int) maxHp);
        }
    }

}