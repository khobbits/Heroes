package com.herocraftonline.dev.heroes.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.MaterialUtil;
import com.herocraftonline.dev.heroes.util.Messaging;

public class RecoverItemsCommand extends BaseCommand {

    public RecoverItemsCommand(Heroes plugin) {
        super(plugin);
        setName("Recover Items");
        setDescription("Recover removed items");
        setUsage("/hero recover");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("hero recover");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Hero h = this.plugin.getHeroManager().getHero(p);

            List<ItemStack> items = h.getRecoveryItems();
            List<ItemStack> newItems = new ArrayList<ItemStack>();

            if (!(items.size() > 0)) {
                Messaging.send(p, "You have no items to recover");
            }

            for (int i = 0; i < items.size(); i++) {
                int slot = this.plugin.getInventoryChecker().firstEmpty(p);
                if (slot == -1) {
                    newItems.add(items.get(i));
                    continue;
                }
                p.getInventory().setItem(slot, items.get(i));
                Messaging.send(p, "Recovered Item $1 - $2", "#" + (i + 1), MaterialUtil.getFriendlyName(items.get(i).getType()));
            }

            if (newItems.size() > 0) {
                Messaging.send(p, "You have $1 left to recover.", newItems.size() + " Items");
            }
            h.setRecoveryItems(newItems);
        }
    }
}
