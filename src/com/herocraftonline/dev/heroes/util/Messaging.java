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
    
    public static String createFullHealthBar(double health, double maxHealth) {
        return "§aHP: §f" + (int) Math.ceil(health) + "/" + (int) Math.ceil(maxHealth) + " " + createHealthBar(health, maxHealth);
    }
    
    public static String createHealthBar(double health, double maxHealth) {
        String healthBar = ChatColor.RED + "[" + ChatColor.GREEN;
        int progress = (int) (health / maxHealth * 50.0);
        for (int i = 0; i < progress; i++) {
            healthBar += "|";
        }
        healthBar += ChatColor.DARK_RED;
        for (int i = 0; i < 50 - progress; i++) {
            healthBar += "|";
        }
        healthBar += ChatColor.RED + "]";
        return healthBar + " - " + ChatColor.GREEN + (int) (health/maxHealth * 100.0) + "%";
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
