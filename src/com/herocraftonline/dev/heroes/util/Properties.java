package com.herocraftonline.dev.heroes.util;

import java.util.HashMap;
import java.util.Map;

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
    // Experience//
    public double partyBonus = 0;
    public boolean resetExpOnClassChange = true;
    public int blockTrackingDuration;
    public int maxTrackedBlocks;
    public double playerKillingExp = 0;
    public Map<CreatureType, Double> creatureKillingExp = new HashMap<CreatureType, Double>();
    public Map<Material, Double> miningExp = new HashMap<Material, Double>();
    public Map<Material, Double> loggingExp = new HashMap<Material, Double>();
    public Map<Material, Double> craftingExp = new HashMap<Material, Double>();

    public Map<String, String> skillInfo = new HashMap<String, String>();
    public Map<Player, Location> playerDeaths = new HashMap<Player, Location>();

    // Default//
    public String defClass;
    public int defLevel;

    // Properties//
    public boolean iConomy;
    public ChatColor cColor;
    public String prefix;
    public int swapCost;
    public boolean swapMasteryCost;
    public boolean damageSystem;

    //Bed Stuffs
    public boolean bedHeal;
    public int healInterval;
    public int healPercent;

    // Map Stuffs
    public boolean mapUI;
    public byte mapID;
    public int mapPacketInterval;

    // Stupid Hats...
    public boolean allowHats;

    /**
     * Generate experience for the level ArrayList<Integer>
     */
    public void calcExp() {
        levels = new int[maxLevel];

        double A = maxExp * Math.pow(maxLevel - 1, -(power + 1));
        for (int i = 0; i < maxLevel; i++) {
            levels[i] = (int) (A * Math.pow(i, power + 1));
        }
        levels[maxLevel - 1] = maxExp;
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
        if (entity == null) return type;
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
