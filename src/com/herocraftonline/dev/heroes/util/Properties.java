package com.herocraftonline.dev.heroes.util;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

public class Properties {

    // Debug Mode //
    public boolean debug;

    // Persistence //
    public String host;
    public String port;
    public String database;
    public String username;
    public String password;
    public String method;

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
    public HashMap<CreatureType, Double> creatureKillingExp = new HashMap<CreatureType, Double>();
    public HashMap<Material, Double> miningExp = new HashMap<Material, Double>();
    public HashMap<Material, Double> loggingExp = new HashMap<Material, Double>();
    public HashMap<Material, Double> craftingExp = new HashMap<Material, Double>();

    public HashMap<String, String> skillInfo = new HashMap<String, String>();
    public HashMap<Player, Location> playerDeaths = new HashMap<Player, Location>();
    // Default//
    public String defClass;
    public int defLevel;
    // Properties//
    public boolean iConomy;
    public ChatColor cColor;
    public String prefix;
    public int swapCost;
    public boolean swapMasteryCost;

    /**
     * Generate experience for the level ArrayList<Integer>
     */
    public void calcExp() {
        levels = new int[maxLevel];

        double A = maxExp * Math.pow(maxLevel - 1, -(power + 1));
        for (int i = 0; i < maxLevel; i++) {
            levels[i] = (int) (A * Math.pow(i, power + 1));
        }
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

    public double getExperience(int level) {
        return levels[level - 1];
    }
}
