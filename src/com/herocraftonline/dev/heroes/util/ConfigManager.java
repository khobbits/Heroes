package com.herocraftonline.dev.heroes.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClassManager;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.OutsourcedSkill;

@SuppressWarnings("deprecation")
public class ConfigManager {

    protected final Heroes plugin;
    protected final File classConfigFolder;
    protected final File expConfigFile;
    protected final File skillConfigFile;
    protected final File outsourcedSkillConfigFile;
    protected final File damageConfigFile;

    public ConfigManager(Heroes plugin) {
        this.plugin = plugin;
        this.classConfigFolder = new File(plugin.getDataFolder() + File.separator + "classes");
        this.expConfigFile = new File(plugin.getDataFolder(), "experience.yml");
        this.skillConfigFile = new File(plugin.getDataFolder(), "skills.yml");
        this.outsourcedSkillConfigFile = new File(plugin.getDataFolder(), "permission-skills.yml");
        this.damageConfigFile = new File(plugin.getDataFolder(), "damages.yml");
    }

    public void load() throws Exception {
        checkForConfig(expConfigFile);
        checkForConfig(damageConfigFile);
        checkForConfig(new File(plugin.getDataFolder(), "font.png"));
        checkForConfig(new File(plugin.getDataFolder(), "heroes.png"));
        checkForConfig(outsourcedSkillConfigFile);
        if (!classConfigFolder.exists()) {
            classConfigFolder.mkdirs();
            checkForConfig(new File(classConfigFolder, "vagrant.yml"));
        }
    }

    public void loadManagers() {
        Configuration damageConfig = new Configuration(damageConfigFile);
        damageConfig.load();
        plugin.getDamageManager().load(damageConfig);

        Configuration expConfig = new Configuration(expConfigFile);
        expConfig.load();
        loadExperience(expConfig);

        HeroClassManager heroClassManager = new HeroClassManager(plugin);
        heroClassManager.loadClasses(classConfigFolder);
        plugin.setClassManager(heroClassManager);
    }

    public void loadSkillConfig(Skill skill) {
        Configuration config = null;
        if (!(skill instanceof OutsourcedSkill)) 
            config = new Configuration(skillConfigFile);
        else {
            config = new Configuration(outsourcedSkillConfigFile);
        }

        config.load();
        ConfigurationNode defaultNode = skill.getDefaultConfig();
        mergeNodeToConfig(config, defaultNode, skill.getName());
        ConfigurationNode loadedNode = config.getNode(skill.getName());
        if (loadedNode == null)
            skill.setConfig(defaultNode);
        else
            skill.setConfig(config.getNode(skill.getName()));
        config.save();
        skill.init();
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


    private void loadExperience(Configuration config) {
        List<String> keys = config.getKeys("killing");
        if (keys != null) {
            for (String item : keys) {
                try {
                    double exp = config.getDouble("killing." + item, 0);
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

        Heroes.properties.miningExp = loadMaterialExperience(config, "mining");
        Heroes.properties.farmingExp = loadMaterialExperience(config, "farming");
        Heroes.properties.loggingExp = loadMaterialExperience(config, "logging");
        Heroes.properties.craftingExp = loadMaterialExperience(config, "crafting");
    }

    private Map<Material, Double> loadMaterialExperience(ConfigurationNode config, String path) {
        Map<Material, Double> expMap = new HashMap<Material, Double>();
        List<String> keys = config.getKeys(path);
        if (keys != null) {
            for (String item : keys) {
                double exp = config.getDouble(path + "." + item, 0);
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

    private void mergeNodeToConfig(Configuration config, ConfigurationNode node, String path) {
        List<String> keys = node.getKeys(null);
        if (keys != null) {
            for (String key : keys) {
                Object value = config.getProperty(path + "." + key);
                if (value == null) {
                    config.setProperty(path + "." + key, node.getProperty(key));
                }
            }
        }
    }
}
