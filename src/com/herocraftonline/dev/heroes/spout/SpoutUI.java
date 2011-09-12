package com.herocraftonline.dev.heroes.spout;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import com.herocraftonline.dev.heroes.Heroes;

public class SpoutUI {
    
    public static void sendPlayerNotification(Player player, String title, String Body, Material material) {
        if(!Heroes.useSpout) {
            return;
        }
        if(SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
            SpoutManager.getPlayer(player).sendNotification(title, Body, material);
        }
    }
}
