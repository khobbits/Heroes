package com.herocraftonline.dev.heroes.command.skill;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Listener;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public abstract class Skill extends BaseCommand {

    public final String SETTING_LEVEL = "level";

    private ConfigurationNode config;

    public Skill(Heroes plugin) {
        super(plugin);
    }

    public void init() {
    }

    protected void notifyNearbyPlayers(Location source, String message, String... args) {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.isSuppressing(this)) {
                continue;
            }
            if (source.getWorld().equals(playerLocation.getWorld())) {
                if (playerLocation.toVector().distance(source.toVector()) < 30) {
                    Messaging.send(player, message, args);
                }
            }
        }
    }

    protected void registerEvent(Type type, Listener listener, Priority priority) {
        plugin.getServer().getPluginManager().registerEvent(type, listener, priority, plugin);
    }

    protected boolean meetsLevelRequirement(Hero hero, int reqLevel) {
        return plugin.getConfigManager().getProperties().getLevel(hero.getExperience()) >= reqLevel;
    }

    public ConfigurationNode getDefaultConfig() {
        return Configuration.getEmptyNode();
    }

    public double getSetting(HeroClass heroClass, String setting, double def) {
        List<String> keys = heroClass == null ? null : heroClass.getSkillSettings(name).getKeys(null);
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(name).getDouble(setting, def);
        } else {
            return config.getDouble(setting, def);
        }
    }

    public int getSetting(HeroClass heroClass, String setting, int def) {
        List<String> keys = heroClass == null ? null : heroClass.getSkillSettings(name).getKeys(null);
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(name).getInt(setting, def);
        } else {
            return config.getInt(setting, def);
        }
    }

    public String getSetting(HeroClass heroClass, String setting, String def) {
        List<String> keys = heroClass == null ? null : heroClass.getSkillSettings(name).getKeys(null);
        if (keys != null && keys.contains(setting)) {
            return heroClass.getSkillSettings(name).getString(setting, def);
        } else {
            return config.getString(setting, def);
        }
    }

    public void setConfig(ConfigurationNode config) {
        this.config = config;
    }

}
