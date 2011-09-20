package com.herocraftonline.dev.heroes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;

public class HSpoutInventoryListener extends InventoryListener {

    private Heroes plugin;

    public HSpoutInventoryListener(Heroes heroes) {
        plugin = heroes;
    }
    
    @Override
    public void onInventoryCraft(InventoryCraftEvent event) {
        if (event.getResult() == null)
            return;
        if (event.getPlayer().getInventory().firstEmpty() == -1)
            return;

        ItemStack result = event.getResult();
        if (event.getCursor() != null)
            return;
        if (plugin.getConfigManager().getProperties().craftingExp.containsKey(result.getType())) {
            Player player = event.getPlayer();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.getHeroClass().getExperienceSources().contains(ExperienceType.CRAFTING)) {
                hero.gainExp(plugin.getConfigManager().getProperties().craftingExp.get(result.getType()), ExperienceType.CRAFTING);
                return;
            }
        }
    }
}
