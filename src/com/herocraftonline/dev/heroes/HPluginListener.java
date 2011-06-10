package com.herocraftonline.dev.heroes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.register.payment.Methods;

/**
 * Checks for plugins whenever one is enabled
 */
public class HPluginListener extends ServerListener {
    private Heroes plugin;
    private Methods Methods = null;

    public HPluginListener(Heroes instance) {
        this.plugin = instance;
        this.Methods = new Methods();
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        // Check if the name is Permissions.
        if (plugin.getDescription().getName().equals("Permissions")) {
            // Check if we haven't already setup Permissions.
            if (Heroes.Permissions == null) {
                // Run the Permissions Setup.
                this.plugin.setupPermissions();
            }
        }

        // Check if the name is BukkitContrib.
        if (plugin.getDescription().getName().equals("BukkitContrib")) {
            this.plugin.setupBukkitContrib();
        }

        // Check to see if we need a payment method
        if (!this.Methods.hasMethod()) {
            if (this.Methods.setMethod(event.getPlugin())) {
                // You might want to make this a public variable inside your MAIN class public Method Method = null;
                // then reference it through this.plugin.Method so that way you can use it in the rest of your plugin ;)
                this.plugin.Method = this.Methods.getMethod();
            }
        }
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        // Check if the name is BukkitContrib.
        if (plugin.getDescription().getName().equals("BukkitContrib")) {
            // If BukkitContrib just Disabled then we tell Heroes to stop using BukkitContrib
            Heroes.useBukkitContrib = false;
            // Then we swap all the Players NSH to our Custom NSH.
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                this.plugin.switchToHNSH(player);
            }
        }

        // Check to see if the plugin thats being disabled is the one we are using
        if (this.Methods != null && this.Methods.hasMethod()) {
            Boolean check = this.Methods.checkDisabled(event.getPlugin());

            if (check) {
                this.plugin.Method = null;
            }
        }
    }
}
