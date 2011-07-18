package com.herocraftonline.dev.heroes.command.commands;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManaCommand extends BaseCommand {

    public ManaCommand(Heroes plugin) {
        super(plugin);
        setName("Mana");
        setDescription("Displays your current mana");
        setUsage("/level");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("mana");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            int mana = hero.getMana();
            player.sendMessage("§9Mana: §f" + mana + " " + Messaging.createManaBar(mana));
        }
    }

}
