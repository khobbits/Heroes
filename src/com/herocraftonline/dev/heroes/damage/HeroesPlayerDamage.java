package com.herocraftonline.dev.heroes.damage;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Properties;

public class HeroesPlayerDamage extends EntityListener {

    public Heroes plugin;
    public HeroesDamage heroesDamage;

    public HeroesPlayerDamage(Heroes plugin, HeroesDamage heroesDamage) {
        this.plugin = plugin;
        this.heroesDamage = heroesDamage;
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Properties prop = plugin.getConfigManager().getProperties();

        if (prop.damageSystem) {

            if (!prop.damageValues.containsKey(damager.getItemInHand().getType())) {
                return;
            }
            Integer damage = prop.damageValues.get(damager.getItemInHand().getType());

            if (event.getEntity() instanceof Player) {
                Player playerEntity = (Player) event.getEntity();
                Hero heroEntity = plugin.getHeroManager().getHero(playerEntity);
                HeroClass entityClass = heroEntity.getHeroClass();

                heroEntity.setHealth(heroEntity.getHealth() - damage);

                Integer health = (int) ((heroEntity.getHealth() / entityClass.getMaxHealth()) * 20);

                if(playerEntity.getHealth() != health) {
                    playerEntity.damage((int) (playerEntity.getHealth() - (health)));
                }

            } else if (event.getEntity() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) event.getEntity();
                CreatureType creatureType = prop.getCreatureFromEntity(livingEntity);
                
                if(!prop.mobMaxHealth.containsKey(creatureType)) {
                    return;
                }
                
                Double maxHealth = prop.mobMaxHealth.get(creatureType);

                if (!heroesDamage.getMobHealthValues().containsKey(livingEntity.getEntityId())) {
                    heroesDamage.getMobHealthValues().put(livingEntity.getEntityId(), maxHealth);
                }
                
                Integer entityMaxHp = 20;
                
                if(creatureType == CreatureType.GIANT) {
                    entityMaxHp = 50;
                }else if(creatureType == CreatureType.GHAST) {
                    entityMaxHp = 10;
                }
                
                heroesDamage.getMobHealthValues().put(livingEntity.getEntityId(), maxHealth - damage);

                Integer health = (int) ((livingEntity.getHealth() / entityMaxHp) * 20);

                if(livingEntity.getHealth() != health) {
                    livingEntity.damage((int) (livingEntity.getHealth() - (health)));
                }                
            }
        }
    }
}
