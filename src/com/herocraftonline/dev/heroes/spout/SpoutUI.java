package com.herocraftonline.dev.heroes.spout;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.herocraftonline.dev.heroes.Heroes;

public class SpoutUI {
    
    Heroes plugin;
    
    public SpoutUI(Heroes plugin) {
        this.plugin = plugin;
    }
    
    
    public void sendPlayerNotification(Player player, String title, String Body, Material material) {
        if(!Heroes.useSpout) {
            return;
        }
        SpoutPlayer sPlayer = (SpoutPlayer) player;
        if(sPlayer.isSpoutCraftEnabled()) {
            sPlayer.sendNotification(title, Body, material);
        }
    }
}
