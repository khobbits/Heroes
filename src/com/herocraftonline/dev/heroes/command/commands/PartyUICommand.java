package com.herocraftonline.dev.heroes.command.commands;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PartyUICommand extends BaseCommand {

    public PartyUICommand(Heroes plugin) {
        super(plugin);
        setName("Party Map UI");
        setDescription("Gives the Player a Map linked to the Party UI.");
        setUsage("/party ui");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("party ui");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command needs to be run from a Player");
            return;
        }

        Player player = (Player) sender;

        if (!this.plugin.getConfigManager().getProperties().mapUI) {
            player.sendMessage("Map UI is not enabled so this command has been disabled.");
            return;
        }

        int slot = player.getInventory().firstEmpty();
        byte mapID = this.plugin.getConfigManager().getProperties().mapID;

        if (slot == -1) {
            player.sendMessage("You have no space in your Inventory for the Party UI Map.");
            return;
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.MAP && item.getDurability() == mapID) {
                player.sendMessage("You already have a Map in your inventory which is linked to the Party UI.");
                return;
            }
        }

        player.getInventory().setItem(slot, new ItemStack(Material.MAP, 1, mapID));
    }
}