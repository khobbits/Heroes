package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroSaveCommand extends BasicCommand {

    private final Heroes plugin;

    public HeroSaveCommand(Heroes plugin) {
        super("Save");
        this.plugin = plugin;
        setDescription("Saves your hero file");
        setUsage("/hero save");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"hero save"});
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        plugin.getHeroManager().saveHero(player);

        Messaging.send(player, "Your hero has been saved sucessfully!");
        return true;
    }

}
