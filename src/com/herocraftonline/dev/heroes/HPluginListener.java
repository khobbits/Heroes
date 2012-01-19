package com.herocraftonline.dev.heroes;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import org.bukkit.plugin.Plugin;

/**
 * Checks for plugins whenever one is enabled
 */
public class HPluginListener implements Listener {

    private Heroes plugin;

    public HPluginListener(Heroes instance) {
        this.plugin = instance;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        String name = plugin.getDescription().getName();

        // Check if the name is Spout
        if (name.equals("Spout")) {
            // If Spout just Disabled then we tell Heroes to stop using Spout
            Heroes.spout = null;
        }

        if (name.equals("Vault")) {
            Heroes.econ = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        String name = plugin.getDescription().getName();

        // Check if the name is Spout.
        if (name.equals("Spout")) {
            this.plugin.setupSpout();
        }

        // Check for Econ
        if (name.equals("Vault")) {
            if (Heroes.econ == null) {
                this.plugin.setupEconomy();
            }
        }
    }
}
