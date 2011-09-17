package com.herocraftonline.dev.heroes;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

/**
 * Checks for plugins whenever one is enabled
 */
public class HPluginListener extends ServerListener {

    private Heroes plugin;

    public HPluginListener(Heroes instance) {
        this.plugin = instance;
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        String name = plugin.getDescription().getName();
        
        // Check if the name is Spout
        if (name.equals("Spout")) {
            // If BukkitContrib just Disabled then we tell Heroes to stop using BukkitContrib
            Heroes.useSpout = false;
        }
        
        if (name.equals("iConomy") || name.equals("BOSEconomy") || name.equals("Essentials")) {
            this.plugin.econ = null;        
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        String name = plugin.getDescription().getName();
        // Check if the name is Permissions.
        if (name.equals("Permissions")) {
            // Check if we haven't already setup Permissions.
            if (Heroes.Permissions == null) {
                // Run the Permissions Setup.
                this.plugin.setupPermissions();
            }
        }

        // Check if the name is BukkitContrib.
        if (name.equals("BukkitContrib")) {
            this.plugin.setupSpout();
        }
        
        //Check for Econ
        if (name.equals("iConomy") || name.equals("BOSEconomy") || name.equals("Essentials")) {
            if (this.plugin.econ == null)
                this.plugin.setupEconomy();
        }
    }
}
