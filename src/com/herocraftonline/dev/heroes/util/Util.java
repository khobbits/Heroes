package com.herocraftonline.dev.heroes.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public final class Util {
    
    // Default Weapon List
    public final static List<String> defaultWeapons;

    // Blocks that we consider transparent for skills
    public final static Set<Material> transparentBlocks;

    // Byte Set of transparents
    public final static HashSet<Byte> transparentIds;

    //Random number generator
    public final static Random rand = new Random();
    
    static {
        defaultWeapons = new ArrayList<String>();
        defaultWeapons.add("WOOD_SWORD");
        defaultWeapons.add("STONE_SWORD");
        defaultWeapons.add("GOLD_SWORD");
        defaultWeapons.add("DIAMOND_SWORD");

        transparentBlocks = new HashSet<Material>();
        transparentBlocks.add(Material.AIR);
        transparentBlocks.add(Material.SNOW);
        transparentBlocks.add(Material.REDSTONE_WIRE);
        transparentBlocks.add(Material.TORCH);
        transparentBlocks.add(Material.REDSTONE_TORCH_OFF);
        transparentBlocks.add(Material.REDSTONE_TORCH_ON);
        transparentBlocks.add(Material.RED_ROSE);
        transparentBlocks.add(Material.YELLOW_FLOWER);
        transparentBlocks.add(Material.SAPLING);
        transparentBlocks.add(Material.LADDER);
        transparentBlocks.add(Material.STONE_PLATE);
        transparentBlocks.add(Material.WOOD_PLATE);
        transparentBlocks.add(Material.CROPS);
        transparentBlocks.add(Material.LEVER);
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.STATIONARY_WATER);
        transparentBlocks.add(Material.RAILS);
        transparentBlocks.add(Material.POWERED_RAIL);
        transparentBlocks.add(Material.DETECTOR_RAIL);
        transparentBlocks.add(Material.DIODE_BLOCK_OFF);
        transparentBlocks.add(Material.DIODE_BLOCK_ON);

        transparentIds = new HashSet<Byte>();
        transparentIds.add((byte) Material.AIR.getId());
        transparentIds.add((byte) Material.SNOW.getId());
        transparentIds.add((byte) Material.REDSTONE_WIRE.getId());
        transparentIds.add((byte) Material.TORCH.getId());
        transparentIds.add((byte) Material.REDSTONE_TORCH_OFF.getId());
        transparentIds.add((byte) Material.REDSTONE_TORCH_ON.getId());
        transparentIds.add((byte) Material.RED_ROSE.getId());
        transparentIds.add((byte) Material.YELLOW_FLOWER.getId());
        transparentIds.add((byte) Material.SAPLING.getId());
        transparentIds.add((byte) Material.LADDER.getId());
        transparentIds.add((byte) Material.STONE_PLATE.getId());
        transparentIds.add((byte) Material.WOOD_PLATE.getId());
        transparentIds.add((byte) Material.CROPS.getId());
        transparentIds.add((byte) Material.LEVER.getId());
        transparentIds.add((byte) Material.WATER.getId());
        transparentIds.add((byte) Material.STATIONARY_WATER.getId());
        transparentIds.add((byte) Material.RAILS.getId());
        transparentIds.add((byte) Material.POWERED_RAIL.getId());
        transparentIds.add((byte) Material.DETECTOR_RAIL.getId());
        transparentIds.add((byte) Material.DIODE_BLOCK_OFF.getId());
        transparentIds.add((byte) Material.DIODE_BLOCK_ON.getId());
    }
    
    /**
     * Checks if the material is a Weapon/Tool
     * 
     * @param mat
     * @return
     */
    public static boolean isWeapon(Material mat) {
        switch(mat) {
        
        case IRON_AXE :
        case IRON_HOE :
        case IRON_PICKAXE :
        case IRON_SPADE :
        case IRON_SWORD :
        case STONE_AXE :
        case STONE_HOE :
        case STONE_PICKAXE :
        case STONE_SPADE :
        case STONE_SWORD :
        case GOLD_AXE :
        case GOLD_HOE :
        case GOLD_PICKAXE :
        case GOLD_SPADE :
        case GOLD_SWORD :
        case WOOD_AXE :
        case WOOD_HOE :
        case WOOD_PICKAXE :
        case WOOD_SPADE :
        case WOOD_SWORD :
        case DIAMOND_AXE :
        case DIAMOND_HOE :
        case DIAMOND_PICKAXE :
        case DIAMOND_SPADE :
        case DIAMOND_SWORD :
            return true;

        default: 
            return false;
        }
    }
    
    /**
     * Tests whether the entity is nearby a spawner
     * 
     * @param entity
     * @param radius
     * @return
     */
    public static boolean isNearSpawner(Entity entity, int radius) {
        Location location = entity.getLocation();

        for (int i = 0 - radius; i <= radius; i++) {
            for (int j = 0 - radius; j <= radius; j++) {
                for (int k = 0 - radius; k <= radius; k++) {
                    if (location.getBlock().getRelative(i, j, k).getType().equals(Material.MOB_SPAWNER))
                        return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Converts an entity into its CreatureType
     * 
     * @param entity
     * @return
     */
    public static CreatureType getCreatureFromEntity(Entity entity) {
        CreatureType type = null;
        if (entity == null)
            return type;
        try {
            Class<?>[] interfaces = entity.getClass().getInterfaces();
            for (Class<?> c : interfaces) {
                if (LivingEntity.class.isAssignableFrom(c)) {
                    type = CreatureType.fromName(c.getSimpleName());
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
        }
        return type;
    }
}
