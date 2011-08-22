package com.herocraftonline.dev.heroes.skill;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Listener;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

/**
 * The root class of the skill heirarchy. This class implements the basic functionality of every Heroes skill including
 * configuration handling, area-based player notifications and event registration. Because this class extends {@link com.herocraftonline.dev.heroes.command.BaseCommand}, the constructor of every skill should define a name,
 * description, usage, min and max arguments and at least one identifier. Any registered events must provide an event
 * listener, usually created as an inner class.
 * </br>
 * </br>
 * <b>Skill Framework:</b>
 * <ul>
 * <li>{@link ActiveSkill}</li>
 * <ul>
 * <li>{@link TargettedSkill}</li>
 * </ul>
 * <li>{@link PassiveSkill}</li> <li>{@link OutsourcedSkill}</li> </ul>
 * <b>Note:</b> All skill identifiers <i>must</i> begin with <i>skill</i>, e.g. "skill fireball".
 */
public abstract class Skill extends BasicCommand {

    private final Heroes plugin;
    private ConfigurationNode config;

    /**
     * The constructor of every skill must define:
     * <ul>
     * <li><code>name</code></li>
     * <li><code>description</code></li>
     * <li><code>usage</code></li>
     * <li><code>minArgs</code></li>
     * <li><code>maxArgs</code></li>
     * <li><code>identifiers</code></li>
     * <li><code>notes</code> (optional)</li>
     * </ul>
     * 
     * @param plugin the active Heroes instance
     */
    public Skill(Heroes plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    public final Heroes getPlugin() {
        return plugin;
    }

    /**
     * Helper method that broadcasts a message to all players within 30 blocks of the specified source. These messages
     * can be suppressed by players on an individual basis.
     * 
     * @param source the <code>Location</code> to measure from
     * @param message the content of the message
     * @param args any text in the message of the format $<i>n</i> where <i>n</i>
     *        is an integer will be replaced with the <i>n</i>th element of
     *        this array
     */
    public void broadcast(Location source, String message, Object... args) {
        if (message.isEmpty()) {
            return;
        }

        final Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.isSuppressing(this)) {
                continue;
            }
            if (source.getWorld().equals(playerLocation.getWorld())) {
                if (playerLocation.distance(source) < 30) {
                    Messaging.send(player, message, args);
                }
            }
        }
    }

    /**
     * The end of the execution path of a skill, this method is called whenever a command with a registered identifier
     * is used.
     * 
     * @param sender the <code>CommandSender</code> issuing the command
     * @param args the arguments provided with the command
     */
    @Override
    public abstract boolean execute(CommandSender sender, String identifier, String[] args);

    /**
     * Creates and returns a <code>ConfigurationNode</code> containing all the default data for the skill. By default,
     * this configuration is empty.
     * 
     * @return an empty configuration
     */
    public ConfigurationNode getDefaultConfig() {
        return Configuration.getEmptyNode();
    }

    public ConfigurationNode getConfig() {
        return config;
    }

    /**
     * Retrieves a <code>double</code> value from the skill's configuration. Data from the provided <code>HeroClass</code> will be preferred over the skill's own data, if found. If the setting is found in neither
     * of these sources, the default value is returned.
     * 
     * @param heroClass the class to search for skill data
     * @param setting the name of the data entry to retrieve
     * @param def the default value to be used if no entry is found
     * @return the stored setting
     */
    public double getSetting(HeroClass heroClass, String setting, double def) {
        List<String> keys = null;
        if (!(heroClass == null)) {
            if (heroClass.getSkillSettings(getName()) != null) {
                keys = heroClass.getSkillSettings(getName()).getKeys();
            }
        }
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(getName()).getDouble(setting, def);
        } else {
            return config.getDouble(setting, def);
        }
    }

    /**
     * Retrieves a <code>int</code> value from the skill's configuration. Data from the provided <code>HeroClass</code> will be preferred over the skill's own data, if found. If the setting is found in neither of these sources, the
     * default value is returned.
     * 
     * @param heroClass the class to search for skill data
     * @param setting the name of the data entry to retrieve
     * @param def the default value to be used if no entry is found
     * @return the stored setting
     */
    public int getSetting(HeroClass heroClass, String setting, int def) {
        List<String> keys = null;
        if (!(heroClass == null)) {
            if (heroClass.getSkillSettings(getName()) != null) {
                keys = heroClass.getSkillSettings(getName()).getKeys();
            }
        }
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(getName()).getInt(setting, def);
        } else {
            return config.getInt(setting, def);
        }
    }

    /**
     * Retrieves a <code>String</code> value from the skill's configuration. Data from the provided <code>HeroClass</code> will be preferred over the skill's own data, if found. If the setting is found in neither
     * of these sources, the default value is returned.
     * 
     * @param heroClass the class to search for skill data
     * @param setting the name of the data entry to retrieve
     * @param def the default value to be used if no entry is found
     * @return the stored setting
     */
    public String getSetting(HeroClass heroClass, String setting, String def) {
        List<String> keys = null;
        if (!(heroClass == null)) {
            if (heroClass.getSkillSettings(getName()) != null) {
                keys = heroClass.getSkillSettings(getName()).getKeys();
            }
        }
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(getName()).getString(setting, def);
        } else {
            return config.getString(setting, def);
        }
    }
    
    /**
     * Retrieves a <code>List of String</code> values from the skill's configuration. Data from the provided <code>HeroClass</code> will be preferred over the skill's own data, if found. If the setting is found in neither
     * of these sources, the default value is returned.
     * 
     * @param heroClass the class to search for skill data
     * @param setting the name of the data entry to retrieve
     * @param def the default value to be used if no entry is found
     * @return the stored setting
     */
    public List<String> getSetting(HeroClass heroClass, String setting, List<String> def) {
        List<String> keys = null;
        if (!(heroClass == null)) {
            if (heroClass.getSkillSettings(getName()) != null) {
                keys = heroClass.getSkillSettings(getName()).getKeys();
            }
        }
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(getName()).getStringList(setting, def);
        } else {
            return config.getStringList(setting, def);
        }
    }

    /**
     * Retrieves a <code>Boolean</code> value from the skill's configuration. Data from the provided <code>HeroClass</code> will be preferred over the skill's own data, if found. If the setting is found in neither
     * of these sources, the default value is returned.
     * 
     * @param heroClass the class to search for skill data
     * @param setting the name of the data entry to retrieve
     * @param def the default value to be used if no entry is found
     * @return the stored setting
     */
    public boolean getSetting(HeroClass heroClass, String setting, boolean def) {
        List<String> keys = null;
        if (!(heroClass == null)) {
            if (heroClass.getSkillSettings(getName()) != null) {
                keys = heroClass.getSkillSettings(getName()).getKeys();
            }
        }
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(getName()).getBoolean(setting, def);
        } else {
            return config.getBoolean(setting, def);
        }
    }
    
    /**
     * Retrieves a <code>List of String</code> keys from the skill's configuration. Data from the provided <code>HeroClass</code> will be preferred over the skill's own data, if found. If the setting is found in neither
     * of these sources, null is returned.
     * 
     * @param heroClass the class to search for skill data
     * @return the stored setting
     */
    public List<String> getSettingKeys(HeroClass heroClass) {
        if (!(heroClass == null)) {
            if (heroClass.getSkillSettings(getName()) != null) {
                return heroClass.getSkillSettings(getName()).getKeys();
            }
        }
        return config.getKeys();
    }
    
    /**
     * An initialization method called after all configuration data is loaded.
     */
    public abstract void init();

    /**
     * Sets the configuration containing all settings related to the skill. This should only be used by the skill loader
     * in most cases.
     * 
     * @param config the new skill configuration
     */
    public void setConfig(ConfigurationNode config) {
        this.config = config;
    }

    /**
     * Helper method to make registering an event a little easier.
     * 
     * @param type the type of event
     * @param listener the listener used to handle the event
     * @param priority the priority given to the event handler
     */
    protected void registerEvent(Type type, Listener listener, Priority priority) {
        plugin.getServer().getPluginManager().registerEvent(type, listener, priority, plugin);
    }

    @Override
    public boolean isShownOnHelpMenu() {
        return false;
    }

}
