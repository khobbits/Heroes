package com.herocraftonline.dev.heroes;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class HBlockListener extends BlockListener {

    private final Heroes plugin;
    private int blockTrackingDuration = 0;
    public static Map<Location, Long> placedBlocks;

    public HBlockListener(Heroes plugin) {
        this.plugin = plugin;
    }

    public void init() {
        final int maxTrackedBlocks = Heroes.properties.maxTrackedBlocks;
        blockTrackingDuration = Heroes.properties.blockTrackingDuration;
        placedBlocks = new LinkedHashMap<Location, Long>() {
            private static final long serialVersionUID = 2623620773233514414L;
            private final int MAX_ENTRIES = maxTrackedBlocks;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Location, Long> eldest) {
                return size() > MAX_ENTRIES || eldest.getValue() + blockTrackingDuration <= System.currentTimeMillis();
            }
        };
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Properties prop = Heroes.properties;
        Player player = event.getPlayer();

        if (prop.disabledWorlds.contains(player.getWorld().getName()))
            return;

        Block block = event.getBlock();

        // Get the Hero representing the player
        Hero hero = plugin.getHeroManager().getHero(player);

        double addedExp = 0;

        ExperienceType et = null;
        if (prop.miningExp.containsKey(block.getType())) {
            addedExp = prop.miningExp.get(block.getType());
            et = ExperienceType.MINING;
        }
        if(prop.farmingExp.containsKey(block.getType())) {
            double newExp = prop.farmingExp.get(block.getType());
            if (newExp > addedExp) {
                addedExp = newExp;
                et = ExperienceType.FARMING;
            }
        }
        if (prop.loggingExp.containsKey(block.getType())) {
            double newExp = prop.loggingExp.get(block.getType());
            if (newExp > addedExp) {
                addedExp = newExp;
                et = ExperienceType.LOGGING;
            }
        }
        if (addedExp == 0) {
            return;
        } else if (wasBlockPlaced(block)) {
            if (hero.isVerbose()) {
                Messaging.send(player, "No experience gained - block placed too recently.");
            }
            placedBlocks.remove(block.getLocation());
            return;
        } else {
            if (hero.hasParty()) {
                hero.getParty().gainExp(addedExp, et, event.getBlock().getLocation());
            } else if (hero.canGain(et)) {
                hero.gainExp(addedExp, et);
            }
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        Block block = event.getBlock();
        Material material = block.getType();

        Properties prop = Heroes.properties;
        if (prop.disabledWorlds.contains(block.getWorld().getName()))
            return;
        
        if (prop.buildingExp.containsKey(material)) {
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (hero.hasExperienceType(ExperienceType.BUILDING) && !hero.hasParty()) {
                hero.gainExp(prop.buildingExp.get(material), ExperienceType.BUILDING);
            } else if (hero.hasParty()) {
                hero.getParty().gainExp(prop.buildingExp.get(material), ExperienceType.BUILDING, event.getBlock().getLocation());
            }
        }
        
        
        
        if (prop.miningExp.containsKey(material) || prop.loggingExp.containsKey(material) || prop.farmingExp.containsKey(material)) {
            placedBlocks.put(block.getLocation().clone(), System.currentTimeMillis());
        }
    }

    
    private boolean wasBlockPlaced(Block block) {
        Location loc = block.getLocation();

        if (placedBlocks.containsKey(loc)) {
            long timePlaced = placedBlocks.get(loc);
            if (timePlaced + blockTrackingDuration > System.currentTimeMillis()) {
                return true;
            } else {
                placedBlocks.remove(loc);
                return false;
            }
        }
        return false;
    }
}
