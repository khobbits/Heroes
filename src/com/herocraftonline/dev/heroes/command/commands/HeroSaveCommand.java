package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroSaveCommand extends BaseCommand {

    public HeroSaveCommand(Heroes plugin) {
        super(plugin);
        name = "HeroSave";
        description = "Saves your hero file";
        usage = "/hero save";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("hero save");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            plugin.getHeroManager().saveHeroFile(player);
            Messaging.send(player, "Your hero has been saved sucessfully!", (String[]) null);
        }
    }

}
