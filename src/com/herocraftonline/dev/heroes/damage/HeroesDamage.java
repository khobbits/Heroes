package com.herocraftonline.dev.heroes.damage;

import java.util.HashMap;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;

import com.herocraftonline.dev.heroes.Heroes;

public class HeroesDamage {
    public Heroes plugin;
    private HeroesDamageListener listener;

    public HeroesDamage(Heroes plugin) {
        this.plugin = plugin;
        listener = new HeroesDamageListener(  plugin, this);
    }

    /**
     * Register the events for the damage system
     */
    public void registerEvents() {
        if(plugin.getConfigManager().getProperties().damageSystem) {
            PluginManager pluginManager = plugin.getServer().getPluginManager();
            pluginManager.registerEvent(Type.ENTITY_DAMAGE, listener, Priority.Highest, plugin);
            pluginManager.registerEvent(Type.CREATURE_SPAWN, listener, Priority.Highest, plugin);
        }
    }

}
