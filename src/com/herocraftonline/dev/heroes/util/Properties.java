package com.herocraftonline.dev.heroes.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.CreatureType;

import com.herocraftonline.dev.heroes.Heroes;

public class Properties {

    // Leveling //
    public double power;
    public static int maxExp;
    public static int maxLevel;
    public static int[] levels;
    public double expLoss;
    public double pvpExpLossMultiplier = 0;
    public boolean levelsViaExpLoss = false;
    public boolean masteryLoss = false;
    public int maxPartySize = 6;
    public double partyBonus = 0;
    public double playerKillingExp = 0;
    public boolean noSpawnCamp = false;
    public int spawnCampRadius;
    public double spawnCampExpMult;
    public boolean resetOnDeath;
    public int pvpLevelRange = 50;
    public boolean orbExp;

    public static double partyMults[];

    // Classes //
    public double swapCost;
    public double oldClassSwapCost;
    public double profSwapCost;
    public double oldProfSwapCost;
    public boolean firstSwitchFree;
    public boolean swapMasterFree;
    public boolean prefixClassName;
    public boolean resetExpOnClassChange = true;
    public boolean resetMasteryOnClassChange = false;
    public boolean resetProfMasteryOnClassChange = false;
    public boolean resetProfOnPrimaryChange = false;
    public boolean lockPathTillMaster = false;
    public boolean lockAtHighestTier = false;

    //Properties
    public boolean debug;
    public String storageType;
    public boolean economy;
    public int blockTrackingDuration;
    public int maxTrackedBlocks;
    public double foodHealPercent = .05;
    public int globalCooldown = 0;
    public double enchantXPMultiplier;
    public boolean slowCasting = true;
    public static int combatTime;

    // Bed Stuffs
    public boolean bedHeal;
    public int healInterval;
    public int healPercent;

    // Mana stuff
    public int manaRegenPercent;
    public int manaRegenInterval;

    // Hats...
    public int hatsLevel;
    public boolean allowHats;

    // Worlds
    public Set<String> disabledWorlds = new HashSet<String>();

    public Map<CreatureType, Double> creatureKillingExp = new EnumMap<CreatureType, Double>(CreatureType.class);
    public Map<Material, Double> miningExp = new EnumMap<Material, Double>(Material.class);
    public Map<Material, Double> farmingExp = new EnumMap<Material, Double>(Material.class);
    public Map<Material, Double> loggingExp = new EnumMap<Material, Double>(Material.class);
    public Map<Material, Double> craftingExp = new EnumMap<Material, Double>(Material.class);
    public Map<Material, Double> buildingExp = new EnumMap<Material, Double>(Material.class);
    public Map<String, String> skillInfo = new HashMap<String, String>();
    public Map<String, RecipeGroup> recipes = new HashMap<String, RecipeGroup>();
    public double fishingExp = 0;
    private Heroes plugin;


    // Potion related
    public double potHealthPerTier;

    public void load(Heroes plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();

        // Load in the data
        loadLevelConfig(config.getConfigurationSection("leveling"));
        loadClassConfig(config.getConfigurationSection("classes"));
        loadProperties(config.getConfigurationSection("properties"));
        loadManaConfig(config.getConfigurationSection("mana"));
        loadBedConfig(config.getConfigurationSection("bed"));
        loadWorldConfig(config.getConfigurationSection("worlds"));
        loadHatsConfig(config.getConfigurationSection("hats"));
    }

    private void loadBedConfig(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        bedHeal = section.getBoolean("enabled", true);
        healInterval = Util.toIntNonNull(section.get("interval", 30), "interval");
        healPercent = Util.toIntNonNull(section.get("percent", 5), "oercent");
    }

    private void loadHatsConfig(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        hatsLevel = Util.toIntNonNull(section.get("level", 1), "level");
        allowHats = section.getBoolean("enabled", false);
    }

    private void loadLevelConfig(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        power = Util.toDoubleNonNull(section.get("exp-curve", 1.00), "exp-curve");
        maxExp = Util.toIntNonNull(section.get("max-exp", 100000), "max-exp");
        maxLevel = Util.toIntNonNull(section.get("max-level", 20), "max-level");
        maxPartySize = Util.toIntNonNull(section.get("max-party-size"), "max-party-size");
        partyBonus = Util.toDoubleNonNull(section.get("party-exp-bonus", 0.20), "party-exp-bonus");
        expLoss = Util.toDoubleNonNull(section.get("exp-loss", 0.05), "expLoss");
        pvpExpLossMultiplier = Util.toDoubleNonNull(section.get("pvp-exp-loss", 1.0), "pvp-exp-loss");
        levelsViaExpLoss = section.getBoolean("level-loss", false);
        masteryLoss = section.getBoolean("mastery-loss", false);
        noSpawnCamp = section.getBoolean("spawner-checks", false);
        spawnCampRadius = Util.toIntNonNull(section.get("spawner-radius", 7), "spawner-radius");
        spawnCampExpMult = Util.toDoubleNonNull(section.get("spawner-exp-mult", .5), "spawner-exp-mult");
        resetOnDeath = section.getBoolean("reset-on-death", false);
        pvpLevelRange = Util.toIntNonNull(section.get("pvp-range", 50), "pvp-range");
        calcExp();
        if (section.getBoolean("dump-exp-file", false)) {
            dumpExpLevels();
        }
        calcPartyMultipliers();
    }

    private void dumpExpLevels() {
        File levelFile = new File(plugin.getDataFolder(), "levels.txt");

        if (levelFile.exists()) {
            levelFile.delete();
        }
        BufferedWriter bos = null;
        try {
            levelFile.createNewFile();
            bos = new BufferedWriter(new FileWriter(levelFile));
            for (int i = 0; i < maxLevel; i++) {
                bos.append(i + " - " + getTotalExp(i + 1) + "\n");
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }
    }

    private void loadClassConfig(ConfigurationSection section) {
        if (section == null) {
            return;
        }

        prefixClassName = section.getBoolean("use-prefix", false);
        resetExpOnClassChange = section.getBoolean("reset-exp-on-change", true);
        resetMasteryOnClassChange = section.getBoolean("reset-master-on-change", false);
        resetProfMasteryOnClassChange = section.getBoolean("reset-prof-master-on-change", false);
        resetProfOnPrimaryChange = section.getBoolean("reset-prof-on-pri-change", false);
        lockPathTillMaster = section.getBoolean("lock-till-master", false);
        lockAtHighestTier = section.getBoolean("lock-at-max-level", false);
        swapMasterFree = section.getBoolean("master-swap-free", true);
        firstSwitchFree = section.getBoolean("first-swap-free", true);
        swapCost = Util.toDoubleNonNull(section.get("swap-cost", 0), "swap-cost");
        oldClassSwapCost = Util.toDoubleNonNull(section.get("old-swap-cost", 0), "old-swap-cost");
        profSwapCost = Util.toDoubleNonNull(section.get("prof-swap-cost", 0.0), "prof-swap-cost");
        oldProfSwapCost = Util.toDoubleNonNull(section.get("old-prof-swap-cost", 0.0), "old-prof-swap-cost");
    }

    private void loadManaConfig(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        manaRegenInterval = Util.toIntNonNull(section.get("interval", 5), "interval");
        manaRegenPercent = Util.toIntNonNull(section.get("percent", 5), "percent");
        // Out of bounds check
        if (manaRegenPercent > 100 || manaRegenPercent < 0) {
            manaRegenPercent = 5;
        }
    }

    private void loadProperties(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        storageType = section.getString("storage-type");
        economy = section.getBoolean("economy", false);
        debug = section.getBoolean("debug", false);
        foodHealPercent = Util.toDoubleNonNull(section.get("food-heal-percent", .05), "food-heal-percent");
        globalCooldown = Util.toIntNonNull(section.get("global-cooldown", 1), "global-cooldown");
        blockTrackingDuration = Util.toIntNonNull(section.get("block-tracking-duration", 10 * 60 * 1000), "block-tracking-duration");
        maxTrackedBlocks = Util.toIntNonNull(section.get("max-tracked-blocks", 1000), "max-tracked-blocks");
        enchantXPMultiplier = Util.toDoubleNonNull(section.get("enchant-exp-mult", 1), "enchant-exp-mult");
        slowCasting = section.getBoolean("slow-while-casting", true);
        combatTime = (int) Util.toDoubleNonNull(section.get("combat-time", 10000), "combat-time");
    }

    private void loadWorldConfig(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        List<String> worlds = section.getStringList("disabled");
        disabledWorlds.addAll(worlds);
    }

    /**
     * Generate experience for the level ArrayList<Integer>
     */
    protected void calcExp() {
        levels = new int[maxLevel];

        double A = maxExp * Math.pow(maxLevel - 1, -(power + 1));
        for (int i = 0; i < maxLevel; i++) {
            levels[i] = (int) (A * Math.pow(i, power + 1));
        }
        levels[maxLevel - 1] = maxExp;
    }

    protected void calcPartyMultipliers() {
        partyMults = new double[maxPartySize];
        for (int i = 0; i < maxPartySize; i++) {
            partyMults[i] = ((maxPartySize - 1.0) / (maxPartySize * Math.log(maxPartySize))) * Math.log(i + 1);
        }
    }

    /**
     * Gets the total amount of experience required to attain the given level
     * @param level
     * @return
     */
    public static int getTotalExp(int level) {
        if (level >= levels.length)
            return levels[levels.length - 1];
        else if (level < 1)
            return levels[0];

        return levels[level - 1];
    }

    /**
     * Gives the exp required to go from the previous level to the level given
     * @param level
     * @return
     */
    public static int getExp(int level) {
        if (level <= 1)
            return 0;
        return getTotalExp(level) - getTotalExp(level - 1);
    }

    /**
     * Convert the given Exp into the correct Level.
     * 
     * @param exp
     * @return
     */
    public static int getLevel(double exp) {
        for (int i = maxLevel - 1; i >= 0; i--) {
            if (exp >= levels[i])
                return i + 1;
        }
        return -1;
    }
}
