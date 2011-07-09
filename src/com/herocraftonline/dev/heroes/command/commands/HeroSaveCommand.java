package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroSaveCommand extends BaseCommand {

    public HeroSaveCommand(Heroes plugin) {
        super(plugin);
        setName("Save");
        setDescription("Saves your hero file");
        setUsage("/hero save");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("hero save");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            plugin.getHeroManager().saveHeroFile(player);
            Messaging.send(player, "Your hero has been saved sucessfully!");
        }
    }

}
