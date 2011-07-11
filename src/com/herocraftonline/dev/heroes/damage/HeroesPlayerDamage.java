package com.herocraftonline.dev.heroes.damage;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Properties;

public class HeroesPlayerDamage extends EntityListener{
    
    public Heroes plugin;
    public HeroesDamage heroesDamage;
    public HeroesPlayerDamage(Heroes plugin, HeroesDamage heroesDamage) {
        this.plugin = plugin;
        this.heroesDamage = heroesDamage;
    }
    
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Properties prop = plugin.getConfigManager().getProperties();
        
        if(prop.damageSystem) { 
            
            if(!prop.damageValues.containsKey(damager.getItemInHand().getType())) {
                return;
            }
            
            if(event.getEntity() instanceof Player) {
                Player playerEntity = (Player) event.getEntity();
                Hero heroEntity = plugin.getHeroManager().getHero(playerEntity);
                // Remove Player HP
                // Update Player Hearts
            }else if(event.getEntity() instanceof Monster){
                Monster monsterEntity = (Monster) event.getEntity();
                if(!heroesDamage.getMobHealthValues().containsKey(monsterEntity.getEntityId())){
                    heroesDamage.getMobHealthValues().put(monsterEntity.getEntityId(), 100);
                }
                // Remove Mob HP
            } 
        }
    }
}
