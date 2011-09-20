package com.herocraftonline.dev.heroes.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.MaterialUtil;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class RecoverItemsCommand extends BasicCommand {
    private final Heroes plugin;

    public RecoverItemsCommand(Heroes plugin) {
        super("Recover Items");
        this.plugin = plugin;
        setDescription("Recover removed items");
        setUsage("/hero recover");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "hero recover" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = this.plugin.getHeroManager().getHero(player);

        List<ItemStack> items = hero.getRecoveryItems();
        List<ItemStack> newItems = new ArrayList<ItemStack>();

        if (!(items.size() > 0)) {
            Messaging.send(player, "You have no items to recover");
            return false;
        }

        for (int i = 0; i < items.size(); i++) {
            int slot = Util.firstEmpty(player);
            if (slot == -1) {
                newItems.add(items.get(i));
                continue;
            }
            player.getInventory().setItem(slot, items.get(i));
            Messaging.send(player, "Recovered Item $1 - $2", "#" + (i + 1), MaterialUtil.getFriendlyName(items.get(i).getType()));
        }

        if (newItems.size() > 0) {
            Messaging.send(player, "You have $1 left to recover.", newItems.size() + " Items");
        }
        hero.setRecoveryItems(newItems);

        return true;
    }
}
