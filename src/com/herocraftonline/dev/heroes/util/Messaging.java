package com.herocraftonline.dev.heroes.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;

public final class Messaging {

    public static void broadcast(Heroes plugin, String msg, Object... params) {
        plugin.getServer().broadcastMessage(parameterizeMessage(msg, params));
    }

    public static String createManaBar(int mana) {
        String manaBar = ChatColor.RED + "[" + ChatColor.BLUE;
        int progress = (int) (mana / 100.0 * 50);
        for (int i = 0; i < progress; i++) {
            manaBar += "|";
        }
        manaBar += ChatColor.DARK_RED;
        for (int i = 0; i < 50 - progress; i++) {
            manaBar += "|";
        }
        manaBar += ChatColor.RED + "]";
        return manaBar + " - " + ChatColor.BLUE + mana + "%";
    }
    
    public static String createFullHealthBar(int health, int d) {
        return "§aHealth: §f" + health + "/" + d + createHealthBar(health, d);
    }
    
    public static String createHealthBar(int health, int maxHealth) {
        String healthBar = ChatColor.RED + "[" + ChatColor.GREEN;
        int progress = (int) (health / maxHealth * 50);
        for (int i = 0; i < progress; i++) {
            healthBar += "|";
        }
        healthBar += ChatColor.DARK_RED;
        for (int i = 0; i < 50 - progress; i++) {
            healthBar += "|";
        }
        healthBar += ChatColor.RED + "]";
        return healthBar + " - " + ChatColor.GREEN + (health/maxHealth) + "%";
    }

    public static void send(CommandSender player, String msg, Object... params) {
        player.sendMessage(parameterizeMessage(msg, params));
    }

    private static String parameterizeMessage(String msg, Object... params) {
        msg = ChatColor.RED + "Heroes: " + ChatColor.GRAY + msg;
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                msg = msg.replace("$" + (i + 1), ChatColor.WHITE + params[i].toString() + ChatColor.GRAY);
            }
        }
        return msg;
    }

}
