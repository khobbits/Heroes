package com.herocraftonline.dev.heroes.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClassManager;
import com.herocraftonline.dev.heroes.skill.SkillManager;

public class ConfigManager {

    protected final Heroes plugin;
    protected static File classConfigFolder;
    protected static File expConfigFile;
    protected static File skillConfigFile;
    protected static File outsourcedSkillConfigFile;
    protected static File damageConfigFile;

    private static Configuration damageConfig;
    private static Configuration expConfig;

    public ConfigManager(Heroes plugin) {
        this.plugin = plugin;
        classConfigFolder = new File(plugin.getDataFolder() + File.separator + "classes");
        expConfigFile = new File(plugin.getDataFolder(), "experience.yml");
        skillConfigFile = new File(plugin.getDataFolder(), "skills.yml");
        outsourcedSkillConfigFile = new File(plugin.getDataFolder(), "permission-skills.yml");
        damageConfigFile = new File(plugin.getDataFolder(), "damages.yml");

        // Load the default Configuration for the skill Manager
        SkillManager.skillConfig = YamlConfiguration.loadConfiguration(skillConfigFile);
        SkillManager.defaultSkillConfig = new MemoryConfiguration();
        SkillManager.skillConfig.setDefaults(SkillManager.defaultSkillConfig);
        SkillManager.skillConfig.options().copyDefaults(true);
        SkillManager.allSkillsConfig = YamlConfiguration.loadConfiguration(outsourcedSkillConfigFile);
        SkillManager.allSkillsConfig.setDefaults(SkillManager.skillConfig);
    }

    public void load() throws Exception {
        checkForConfig(expConfigFile);
        checkForConfig(damageConfigFile);
        checkForConfig(outsourcedSkillConfigFile);
        if (!classConfigFolder.exists()) {
            classConfigFolder.mkdirs();
            checkForConfig(new File(classConfigFolder, "vagrant.yml"));
        }
    }

    public void loadManagers() {
        damageConfig = YamlConfiguration.loadConfiguration(damageConfigFile);
        InputStream defConfigStream = plugin.getResource("defaults" + File.separator + "damages.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            damageConfig.setDefaults(defConfig);
        }
        plugin.getDamageManager().load(damageConfig);


        expConfig = YamlConfiguration.loadConfiguration(expConfigFile);
        defConfigStream = plugin.getResource("defaults" + File.separator + "experience.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            expConfig.setDefaults(defConfig);
        }
        loadExperience();

        HeroClassManager heroClassManager = new HeroClassManager(plugin);
        heroClassManager.loadClasses(classConfigFolder);
        plugin.setClassManager(heroClassManager);
    }

    @SuppressWarnings({ "unchecked" })
    public void print(Map<String, Object> map, String indent) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                plugin.debugLog(Level.INFO, indent + entry.getKey());
                print((Map<String, Object>) entry.getValue(), indent + "  ");
            } else {
                plugin.debugLog(Level.INFO, indent + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    public boolean reload() {
        try {
            final Player[] players = plugin.getServer().getOnlinePlayers();
            for (Player player : players) {
                plugin.getHeroManager().saveHero(player);
            }
            load();
            loadManagers();
        } catch (Exception e) {
            e.printStackTrace();
            Heroes.log(Level.SEVERE, "Critical error encountered while loading. Disabling...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }
        Heroes.log(Level.INFO, "Reloaded Configuration");
        return true;
    }

    private void checkForConfig(File config) {
        if (!config.exists()) {
            try {
                Heroes.log(Level.WARNING, "File " + config.getName() + " not found - generating defaults.");
                config.getParentFile().mkdir();
                config.createNewFile();
                OutputStream output = new FileOutputStream(config, false);
                InputStream input = ConfigManager.class.getResourceAsStream("/defaults/" + config.getName());
                byte[] buf = new byte[8192];
                while (true) {
                    int length = input.read(buf);
                    if (length < 0) {
                        break;
                    }
                    output.write(buf, 0, length);
                }
                input.close();
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadExperience() {
        ConfigurationSection section = expConfig.getConfigurationSection("killing");
        if (section == null)
            return;
        Set<String> keys = section.getKeys(false);
        if (keys != null) {
            for (String item : keys) {
                try {
                    double exp = section.getDouble(item, 0);
                    if (item.equals("player")) {
                        Heroes.properties.playerKillingExp = exp;
                    } else {
                        CreatureType type = CreatureType.valueOf(item.toUpperCase());
                        Heroes.properties.creatureKillingExp.put(type, exp);
                    }
                } catch (IllegalArgumentException e) {
                    Heroes.log(Level.WARNING, "Invalid creature type (" + item + ") found in experience.yml.");
                }
            }
        }

        Heroes.properties.miningExp = loadMaterialExperience(expConfig.getConfigurationSection("mining"));
        Heroes.properties.farmingExp = loadMaterialExperience(expConfig.getConfigurationSection("farming"));
        Heroes.properties.loggingExp = loadMaterialExperience(expConfig.getConfigurationSection("logging"));
        Heroes.properties.craftingExp = loadMaterialExperience(expConfig.getConfigurationSection("crafting"));
    }

    private Map<Material, Double> loadMaterialExperience(ConfigurationSection section) {
        Map<Material, Double> expMap = new HashMap<Material, Double>();
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            for (String item : keys) {
                double exp = section.getDouble(item, 0);
                Material type = Material.matchMaterial(item);

                if (type != null) {
                    expMap.put(type, exp);
                } else {
                    Heroes.log(Level.WARNING, "Invalid material type (" + item + ") found in experience.yml.");
                }
            }
        }
        return expMap;
    }
}
