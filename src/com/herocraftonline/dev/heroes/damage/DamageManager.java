package com.herocraftonline.dev.heroes.damage;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import com.herocraftonline.dev.heroes.Heroes;

public class DamageManager {
    private Heroes plugin;
    private HeroesDamageListener listener;
    private HashMap<Material, Integer> itemDamage;
    private HashMap<CreatureType, Integer> creatureHealth;
    private HashMap<CreatureType, Integer> creatureDamage;
    private HashMap<DamageCause, Integer> environmentalDamage;

    public DamageManager(Heroes plugin) {
        this.plugin = plugin;
        listener = new HeroesDamageListener(plugin, this);
    }

    /**
     * Register the events for the damage system
     */
    public void registerEvents() {
        if (plugin.getConfigManager().getProperties().damageSystem) {
            PluginManager pluginManager = plugin.getServer().getPluginManager();
            pluginManager.registerEvent(Type.ENTITY_DAMAGE, listener, Priority.Highest, plugin);
            pluginManager.registerEvent(Type.CREATURE_SPAWN, listener, Priority.Highest, plugin);
        }
    }

    public int getItemDamage(Material item) {
        if (itemDamage.containsKey(item)) {
            return itemDamage.get(item);
        } else {
            return -1;
        }
    }

    public int getCreatureHealth(CreatureType type) {
        if (creatureHealth.containsKey(type)) {
            int health = creatureHealth.get(type);
            return health > 200 ? 200 : (health < 0 ? health : 0);
        } else {
            return 10;
        }
    }

    public int getCreatureDamage(CreatureType type) {
        if (creatureDamage.containsKey(type)) {
            return creatureDamage.get(type);
        } else {
            return 2;
        }
    }

    public int getEnvironmentalDamage(DamageCause cause) {
        if (environmentalDamage.containsValue(cause)) {
            return environmentalDamage.get(cause);
        } else {
            return 4;
        }
    }

    public void load(Configuration config) {
        List<String> keys;

        creatureHealth = new HashMap<CreatureType, Integer>();
        keys = config.getKeys("creature-health");
        if (keys != null) {
            for (String key : keys) {
                CreatureType type = CreatureType.fromName(key);
                int health = config.getInt("creature-health." + key, 10);
                if (type != null) {
                    creatureHealth.put(type, health);
                }
            }
        }

        creatureDamage = new HashMap<CreatureType, Integer>();
        keys = config.getKeys("creature-damage");
        if (keys != null) {
            for (String key : keys) {
                CreatureType type = CreatureType.fromName(key);
                int damage = config.getInt("creature-damage." + key, 10);
                if (type != null) {
                    creatureDamage.put(type, damage);
                }
            }
        }

        itemDamage = new HashMap<Material, Integer>();
        keys = config.getKeys("item-damage");
        if (keys != null) {
            for (String key : keys) {
                Material item = Material.matchMaterial(key);
                int damage = config.getInt("item-damage." + key, 2);
                if (item != null) {
                    itemDamage.put(item, damage);
                }
            }
        }

        environmentalDamage = new HashMap<DamageCause, Integer>();
        keys = config.getKeys("environmental-damage");
        if (keys != null) {
            for (String key : keys) {
                try {
                    DamageCause cause = DamageCause.valueOf(key.toUpperCase());
                    int damage = config.getInt("environmental-damage." + key, 2);
                    environmentalDamage.put(cause, damage);
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }
        }
    }

}
