package com.herocraftonline.dev.heroes.health;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class HealthReplacement {
    Heroes plugin;
    
    public HealthReplacement(Heroes plugin) {
        this.plugin = plugin;
    }
    
    
    public void setMaxHealth(Hero hero) {
        Player player = hero.getPlayer();
        if(player.isDead()) {
            return;
        }
        //hero.setHealth(100)
    }
    
    public void updatePlayerHealth(Player player) {
        //player.setHealth((hero.getHealth()/hero.getMaxHealth())*20) [10:54:17] <Dgco_Work> just make sure you round UP
    }
}
