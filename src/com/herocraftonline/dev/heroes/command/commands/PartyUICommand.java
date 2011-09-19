package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyUICommand extends BasicCommand {
    private final Heroes plugin;

    public PartyUICommand(Heroes plugin) {
        super("Party Map UI");
        this.plugin = plugin;
        setDescription("Gives the Player a Map linked to the Party UI.");
        setUsage("/party ui");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "party ui" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        if (!this.plugin.getConfigManager().getProperties().mapUI) {
            Messaging.send(sender, "Map UI is not enabled so this command has been disabled.");
            return false;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand != null && itemInHand.getType() != Material.MAP) {
            Messaging.send(sender, "You need to have a Map equipped in your hand to turn it into the Party UI.");
            return false;
        }

        if (itemInHand.getDurability() == this.plugin.getConfigManager().getProperties().mapID) {
            Messaging.send(sender, "This Map is already linked to the Party UI!");
            return false;
        }

        itemInHand.setDurability(this.plugin.getConfigManager().getProperties().mapID);

        Messaging.send(sender, "Your map has been converted to the Party UI");

        Hero hero = this.plugin.getHeroManager().getHero(player);
        if (hero.getParty() != null) {
            hero.getParty().setUpdateMapDisplay(true);
        }

        return true;
    }
}
