package com.herocraftonline.dev.heroes.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Properties {

    // Debug Mode //
    public boolean debug;

    // Leveling//
    public double power;
    public int maxExp;
    public int maxLevel;
    public int[] levels;
    public double expLoss;
    public boolean levelsViaExpLoss = false;
    public boolean masteryLoss = false;
    // Experience//
    public double partyBonus = 0;
    public boolean resetExpOnClassChange = true;
    public int blockTrackingDuration;
    public int maxTrackedBlocks;
    public double playerKillingExp = 0;
    public boolean noSpawnCamp = false;
    public int spawnCampRadius;
    public Map<CreatureType, Double> creatureKillingExp = new HashMap<CreatureType, Double>();
    public Map<Material, Double> miningExp = new HashMap<Material, Double>();
    public Map<Material, Double> loggingExp = new HashMap<Material, Double>();
    public Map<Material, Double> craftingExp = new HashMap<Material, Double>();
    public Map<String, String> skillInfo = new HashMap<String, String>();
    public Map<Player, Location> playerDeaths = new HashMap<Player, Location>();

    // Default//
    public String defClass;
    public int defLevel;
    public boolean resetOnDeath;
    
    // Properties//
    public boolean iConomy;
    public ChatColor cColor;
    public String prefix;
    public int swapCost;
    public boolean firstSwitchFree;
    public boolean swapMasteryCost;
    public boolean damageSystem;

    // Bed Stuffs
    public boolean bedHeal;
    public int healInterval;
    public int healPercent;

    // Mana stuff
    public int manaRegenPercent;
    public int manaRegenInterval;
    
    // Map Stuffs
    public boolean mapUI;
    public byte mapID;
    public int mapPacketInterval;

    // Worlds
    public Set<String> disabledWorlds = new HashSet<String>();

    // Stupid Hats...
    public boolean allowHats;
    
    // Prefix ClassName
    public boolean prefixClassName;

    // Default Weapon List
    public final static List<String> defaultWeapons;
    
    // Blocks that we consider transparent for skills
    public final static Set<Material> transparentBlocks;
    
    // Byte Set of transparents
    public final static HashSet<Byte> transparentIds;
    
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
     * Generate experience for the level ArrayList<Integer>
     */
    public void calcExp() {
        levels = new int[maxLevel + 1];

        double A = maxExp * Math.pow(maxLevel - 1, -(power + 1));
        for (int i = 0; i < maxLevel; i++) {
            levels[i] = (int) (A * Math.pow(i, power + 1));
        }
        levels[maxLevel - 1] = maxExp;
        levels[maxLevel] = (int) (A * Math.pow(maxLevel, power + 1));
    }

    public double getExperience(int level) {
        return levels[level - 1];
    }

    /**
     * Convert the given Exp into the correct Level.
     * 
     * @param exp
     * @return
     */
    public int getLevel(double exp) {
        for (int i = maxLevel - 1; i >= 0; i--) {
            if (exp >= levels[i]) {
                return i + 1;
            }
        }
        return -1;
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
    
    public static Boolean isNearSpawner(Entity entity, int radius)
    {
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
}
