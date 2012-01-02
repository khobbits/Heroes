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
            // If Spout just Disabled then we tell Heroes to stop using Spout
            Heroes.spout = null;
        }

        if (name.equals("iConomy") || name.equals("BOSEconomy") || name.equals("Essentials")) {
            Heroes.econ = null;
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        String name = plugin.getDescription().getName();

        // Check if the name is Spout.
        if (name.equals("Spout")) {
            this.plugin.setupSpout();
        }

        // Check for Econ
        if (name.equals("iConomy") || name.equals("BOSEconomy") || name.equals("Essentials")) {
            if (Heroes.econ == null) {
                this.plugin.setupEconomy();
            }
        }
    }
}
