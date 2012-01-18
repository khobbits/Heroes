package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class ScrollCommand extends BasicCommand {

    private Heroes plugin;
    
    public ScrollCommand(Heroes plugin) {
        super("Scroll");
        this.plugin = plugin;
        setDescription("This will convert a map in your inventory to a scroll!");
        setUsage("/scroll");
        setArgumentRange(0, 0);
        setIdentifiers("hero scroll");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) {
            Messaging.send(sender, "Only players may use that command!");
            return true;
        }
        Player player = (Player) sender;
        int slot = player.getInventory().first(Material.MAP);
        if (slot == -1) {
            Messaging.send(player, "You need a map to convert into a scroll!");
            return true;
        }
        Hero hero = plugin.getHeroManager().getHero(player);
        ItemStack map = player.getInventory().getItem(slot);
        map.setDurability(hero.getHeroClass().getView().getMapView().getId());
        hero.getHeroClass().getView().setDirty(true);
        return true;
    }

}
