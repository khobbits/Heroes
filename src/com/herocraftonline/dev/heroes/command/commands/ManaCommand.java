package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class ManaCommand extends BasicCommand {
    private final Heroes plugin;

    public ManaCommand(Heroes plugin) {
        super("Mana");
        this.plugin = plugin;
        setDescription("Displays your current mana");
        setUsage("/level");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"mana"});
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);

        int mana = hero.getMana();
        player.sendMessage("§9Mana: §f" + mana + " " + Messaging.createManaBar(mana));

        return true;
    }

}
