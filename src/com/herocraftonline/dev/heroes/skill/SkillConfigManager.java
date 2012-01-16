package com.herocraftonline.dev.heroes.skill;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillConfigManager {

    // Configurations
    public static Configuration outsourcedSkillConfig;
    public static Configuration standardSkillConfig;
    public static Configuration defaultSkillConfig = new MemoryConfiguration();

    public static File dataFolder;

    public static Map<String, Configuration> classSkillConfigs = new HashMap<String, Configuration>();
    public static File skillConfigFile;
    public static File outsourcedSkillConfigFile;

    public SkillConfigManager(Heroes plugin) {
        dataFolder = plugin.getDataFolder();
        skillConfigFile = new File(dataFolder, "skills.yml");
        outsourcedSkillConfigFile = new File(dataFolder, "permission-skills.yml");
        plugin.getConfigManager().checkForConfig(outsourcedSkillConfigFile);
    }

    public void reload() {
        standardSkillConfig = null;
        outsourcedSkillConfig = null;
        load();
    }

    public void load() {
        // Setup the standard skill configuration
        standardSkillConfig = YamlConfiguration.loadConfiguration(skillConfigFile);
        standardSkillConfig.setDefaults(defaultSkillConfig);
        standardSkillConfig.options().copyDefaults(true);

        // Setup the outsourced skill configuration
        outsourcedSkillConfig = YamlConfiguration.loadConfiguration(outsourcedSkillConfigFile);
        outsourcedSkillConfig.setDefaults(standardSkillConfig);

        //MERGE!
        for (String key : standardSkillConfig.getKeys(true)) {
            if (standardSkillConfig.isConfigurationSection(key))
                continue;
            outsourcedSkillConfig.set(key, standardSkillConfig.get(key));
        }
    }

    public static void saveSkillConfig() {
        try {
            ((YamlConfiguration) standardSkillConfig).save(skillConfigFile);
        } catch (IOException e) {
            Heroes.log(Level.WARNING, "Unable to save default skills file!");
        }
    }

    public Configuration getClassConfig(String name) {
        return classSkillConfigs.get(name);
    }

    public void addClassSkillSettings(String className, String skillName, ConfigurationSection section) {
        Configuration config = classSkillConfigs.get(className);
        if (config == null) {
            config = new MemoryConfiguration();
            classSkillConfigs.put(className, config);
        }
        if (section == null)
            return;

        ConfigurationSection classSection = config.getConfigurationSection(skillName);
        if (classSection == null)
            classSection = config.createSection(skillName); 

        for (String key : section.getKeys(true)) {
            if (section.isConfigurationSection(key))
                classSection.createSection(key);
        }

        for (String key : section.getKeys(true)) {
            if (section.isConfigurationSection(key))
                continue;

            classSection.set(key, section.get(key));
        }
    }

    public void loadSkillDefaults(Skill skill) {
        if (skill instanceof OutsourcedSkill)
            return;
        ConfigurationSection dSection = skill.getDefaultConfig();
        ConfigurationSection newSection = defaultSkillConfig.createSection(skill.getName());
        //Loop through once and create all the keys
        for (String key : dSection.getKeys(true)) {
            if (dSection.isConfigurationSection(key))
                newSection.createSection(key);
        }
        for (String key : dSection.getKeys(true)) {
            if (dSection.isConfigurationSection(key)) {
                //Skip section as they would overwrite data here
                continue;
            }
            newSection.set(key, dSection.get(key));
        }
    }

    // Because bukkit can't handle setting defaults before sections exist
    public static void setClassDefaults() {
        for (Configuration config : classSkillConfigs.values()) {
            config.setDefaults(outsourcedSkillConfig);
        }
    }

    //------------------------//
    // Data retrieval methods //
    //------------------------//

    /**
     * Gets a string setting at the given path, if the value does not exist in the standard configuration
     * this will instead return the default value
     * 
     * @param skill
     * @param setting
     * @param def
     * @return the String at the path
     */
    public static String getRaw(Skill skill, String setting, String def) {
        return outsourcedSkillConfig.getString(skill.getName() + "." + setting, def);
    }

    /**
     * Gets a string setting at the given path, if the value does not exist in the standard configuration
     * this will instead return the default value
     * 
     * @param skill
     * @param setting
     * @param def
     * @return the String at the path
     */
    public static String getRaw(Skill skill, Setting setting, String def) {
        return getRaw(skill, setting.node(), def);
    }

    /**
     * Gets a boolean setting at the given path, if the value does not exist in the standard configuration
     * this will instead return the default value
     * 
     * @param skill
     * @param setting
     * @param def
     * @return the boolean at the path
     */
    public static boolean getRaw(Skill skill, Setting setting, boolean def) {
        return getRaw(skill, setting.node(), def);
    }

    /**
     * Gets a boolean setting at the given path, if the value does not exist in the standard configuration
     * this will instead return the default value
     * 
     * @param skill
     * @param setting
     * @param def
     * @return the boolean at the path
     */
    public static boolean getRaw(Skill skill, String setting, boolean def) {
        return outsourcedSkillConfig.getBoolean(skill.getName() + "." + setting, def);
    }

    /**
     * Gets the default Keys for the given skill at the given path.
     * If no keys are found or if the path is not a configuration section an empty set will be returned
     * 
     * @param skill
     * @param setting
     * @return Set<String> of keys
     */
    public static Set<String> getRawKeys(Skill skill, String setting) {
        String path = skill.getName();
        if (setting != null)
            path += "." + setting;

        if (!outsourcedSkillConfig.isConfigurationSection(path))
            return new HashSet<String>();

        return outsourcedSkillConfig.getConfigurationSection(path).getKeys(false);
    }

    public static Object getSetting(HeroClass hc, Skill skill, String setting) {
        Configuration config = classSkillConfigs.get(hc.getName());
        if (config == null || !config.isConfigurationSection(skill.getName()))
            return null;
        else
            return config.get(skill.getName() + "." + setting);

    }

    public static int getSetting(HeroClass hc, Skill skill, String setting, int def) {
        Object val = getSetting(hc, skill, setting);
        if (val == null)
            return def;
        else {
            Integer i = Util.toInt(val);
            return i != null ? i : def;
        }
    }

    public static double getSetting(HeroClass hc, Skill skill, String setting, double def) {
        Object val = getSetting(hc, skill, setting);
        if (val == null)
            return def;
        else {
            Double d = Util.toDouble(val);
            return d != null ? d : def;
        }
    }

    public static String getSetting(HeroClass hc, Skill skill, String setting, String def) {
        Object val = getSetting(hc, skill, setting);
        if (val == null)
            return def;
        else
            return val.toString();
    }

    public static boolean getSetting(HeroClass hc, Skill skill, String setting, boolean def) {
        Object val = getSetting(hc, skill, setting);
        if (val == null || !(val instanceof Boolean))
            return def;
        else
            return (Boolean) val;
    }

    public static List<String> getSetting(HeroClass hc, Skill skill, String setting, List<String> def) {
        Configuration config = classSkillConfigs.get(hc.getName());
        if (config == null || !config.isConfigurationSection(skill.getName()))
            return def;

        List<String> val = config.getStringList(skill.getName() + "." + setting);
        return val != null && !val.isEmpty() ? val : def;
    }

    public static Set<String> getSettingKeys(HeroClass hc, Skill skill, String setting) {
        String path = skill.getName();
        if (setting != null)
            path += "." + setting;

        Configuration config = classSkillConfigs.get(hc.getName());
        if (config == null || !config.isConfigurationSection(path))
            return new HashSet<String>();

        return config.getConfigurationSection(path).getKeys(false);
    }

    public static Set<String> getUseSettingKeys(Hero hero, Skill skill, String setting) {
        Set<String> vals = new HashSet<String>();
        if (hero.canPrimaryUseSkill(skill))
            vals.addAll(getSettingKeys(hero.getHeroClass(), skill, setting));
        if (hero.canSecondUseSkill(skill))
            vals.addAll(getSettingKeys(hero.getSecondClass(), skill, setting));

        return vals;
    }

    public static List<String> getUseSettingKeys(Hero hero, Skill skill) {
        Set<String> keys = new HashSet<String>();
        ConfigurationSection section = outsourcedSkillConfig.getConfigurationSection(skill.getName());
        if (section != null)
            keys.addAll(section.getKeys(false));

        if (hero.canPrimaryUseSkill(skill)) {
            keys.addAll(getSettingKeys(hero.getHeroClass(), skill, null));
        }

        if (hero.canSecondUseSkill(skill)) {
            keys.addAll(getSettingKeys(hero.getSecondClass(), skill, null));
        }
        return new ArrayList<String>(keys);
    }

    public static int getLevel(Hero hero, Skill skill, int def) {
        String name = skill.getName();
        if (hero == null)
            return outsourcedSkillConfig.getInt(name + "." + Setting.LEVEL.node(), def);

        int val1 = -1;
        int val2 = -1;
        if (hero.getHeroClass().hasSkill(name)) {
            val1 = getSetting(hero.getHeroClass(), skill, Setting.LEVEL.node(), def);
        }
        if (hero.getSecondClass() != null && hero.getSecondClass().hasSkill(name)) {
            val2 = getSetting(hero.getSecondClass(), skill, Setting.LEVEL.node(), def);
        }

        if (val1 != -1 && val2 != -1) {
            return val1 < val2 ? val1 : val2;
        } else if (val1 != -1)
            return val1;
        else if (val2 != -1)
            return val2;
        else
            return outsourcedSkillConfig.getInt(name + "." + Setting.LEVEL.node(), def);
    }

    public static int getUseSetting(Hero hero, Skill skill, Setting setting, int def, boolean lower) {
        if (setting == Setting.LEVEL)
            return getLevel(hero, skill, def);
        else
            return getUseSetting(hero, skill, setting.node(), def, lower);
    }

    public static String getUseSetting(Hero hero, Skill skill, Setting setting, String def) {
        return getUseSetting(hero, skill, setting.node(), def);
    }

    public static double getUseSetting(Hero hero, Skill skill, Setting setting, double def, boolean lower) {
        return getUseSetting(hero, skill, setting.node(), def, lower);
    }

    public static boolean getUseSetting(Hero hero, Skill skill, Setting setting, boolean def) {
        return getUseSetting(hero, skill, setting.node(), def);
    }

    public static int getUseSetting(Hero hero, Skill skill, String setting, int def, boolean lower) {
        if (setting.equalsIgnoreCase("level"))
            throw new IllegalArgumentException("Do not use getSetting() for grabbing a hero level!");

        String name = skill.getName();
        if (hero == null)
            return outsourcedSkillConfig.getInt(name + "." + setting, def);

        int val1 = -1;
        int val2 = -1;
        if (hero.canPrimaryUseSkill(skill)) {
            val1 = getSetting(hero.getHeroClass(), skill, setting, def);
        }
        if (hero.canSecondUseSkill(skill)) {
            val2 = getSetting(hero.getSecondClass(), skill, setting, def);
        }

        if (val1 != -1 && val2 != -1) {
            if (lower)
                return val1 < val2 ? val1 : val2;
            else
                return val1 > val2 ? val1 : val2;
        } else if (val1 != -1)
            return val1;
        else if (val2 != -1)
            return val2;
        else
            return outsourcedSkillConfig.getInt(name + "." + setting, def);
    }

    public static double getUseSetting(Hero hero, Skill skill, String setting, double def, boolean lower) {
        String name = skill.getName();
        if (hero == null)
            return outsourcedSkillConfig.getDouble(name + "." + setting, def);

        double val1 = -1;
        double val2 = -1;
        if (hero.canPrimaryUseSkill(skill)) {
            val1 = getSetting(hero.getHeroClass(), skill, setting, def);
        }
        if (hero.canSecondUseSkill(skill)) {
            val2 = getSetting(hero.getSecondClass(), skill, setting, def);
        }

        if (val1 != -1 && val2 != -1) {
            if (lower)
                return val1 < val2 ? val1 : val2;
            else
                return val1 > val2 ? val1 : val2;
        } else if (val1 != -1)
            return val1;
        else if (val2 != -1)
            return val2;
        else
            return outsourcedSkillConfig.getDouble(name + "." + setting, def);
    }

    public static boolean getUseSetting(Hero hero, Skill skill, String setting, boolean def) {
        if (hero == null)
            return outsourcedSkillConfig.getBoolean(skill.getName() + "." + setting, def);
        int val1 = -1;
        int val2 = -1;

        if (hero.canPrimaryUseSkill(skill))
            val1 = getSetting(hero.getHeroClass(), skill, setting, def) ? 1 : 0;

        if (hero.canSecondUseSkill(skill))
            val2 = getSetting(hero.getSecondClass(), skill, setting, def) ? 1 :0;

        if (val1 == -1 && val2 == -1)
            return def;
        else if (val2 != -1 && val2 <= val1)
            return val2 == 1 ? true : false;
        else
            return val1 == 1 ? true : false;
    }

    public static String getUseSetting(Hero hero, Skill skill, String setting, String def) {
        if (hero == null) {
            return outsourcedSkillConfig.getString(skill.getName() + "." + setting, def);
        }
        else if (hero.canPrimaryUseSkill(skill))
            return getSetting(hero.getHeroClass(), skill, setting, def);
        else if (hero.canSecondUseSkill(skill))
            return getSetting(hero.getSecondClass(), skill, setting, def);
        else
            return outsourcedSkillConfig.getString(skill.getName() + "." + setting, def);
    }

    public static List<String> getUseSetting(Hero hero, Skill skill, String setting, List<String> def) {
        if (hero == null) {
            List<String> list = outsourcedSkillConfig.getStringList(skill.getName() + "." + setting);
            return list != null ? list : def;
        }

        List<String> vals = new ArrayList<String>();
        if (hero.canPrimaryUseSkill(skill)) {
            List<String> list = getSetting(hero.getHeroClass(), skill, setting, new ArrayList<String>());
            vals.addAll(list);
        } if (hero.canSecondUseSkill(skill)) {
            List<String> list = getSetting(hero.getSecondClass(), skill, setting, new ArrayList<String>());
            vals.addAll(list);
        }
        if (!vals.isEmpty())
            return vals;
        else {
            List<String> list = outsourcedSkillConfig.getStringList(skill.getName() + "." + setting);
            return list != null && !list.isEmpty() ? list : def;
        }
    }
}
