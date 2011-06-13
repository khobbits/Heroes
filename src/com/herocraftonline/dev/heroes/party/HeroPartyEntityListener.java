package com.herocraftonline.dev.heroes.party;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class HeroPartyEntityListener extends EntityListener{
    
    private Heroes plugin;
    
    public HeroPartyEntityListener(Heroes plugin) {
        this.plugin = plugin;
    }
    
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entityPlayer = (Player) event.getEntity();
            Player damagerPlayer = (Player) event.getDamager();
            Hero entityHero = plugin.getHeroManager().getHero(entityPlayer);
            Hero damagerHero = plugin.getHeroManager().getHero(damagerPlayer);
            //if players are in same party
            //if party is pvp off
            //cancel
        }
    }
}
