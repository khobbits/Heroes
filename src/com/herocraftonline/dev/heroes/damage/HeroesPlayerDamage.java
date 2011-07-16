package com.herocraftonline.dev.heroes.damage;

import java.util.logging.Level;

import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet8UpdateHealth;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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

    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
        if (!(subEvent.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) subEvent.getDamager();
        Properties prop = plugin.getConfigManager().getProperties();
        if (prop.damageSystem) {

            if (!prop.damageValues.containsKey(damager.getItemInHand().getType())) {
                return;
            }
            Integer damage = prop.damageValues.get(damager.getItemInHand().getType());

            if (subEvent.getEntity() instanceof Player) {
                Player playerEntity = (Player) subEvent.getEntity();
                Hero heroEntity = plugin.getHeroManager().getHero(playerEntity);
                HeroClass entityClass = heroEntity.getHeroClass();
                
                double iHeroHP = heroEntity.getHealth();
                double iPlayerHP = playerEntity.getHealth();

                heroEntity.setHealth(heroEntity.getHealth() - damage);
                int newHealth = (int) ((heroEntity.getHealth() / entityClass.getMaxHealth()) * 20);
                
                double fHeroHP = heroEntity.getHealth();
                double fPlayerHP = newHealth;

                plugin.log(Level.INFO, "Damage Done: " + iHeroHP + " (" + iPlayerHP + ") --> " + fHeroHP + " (" + fPlayerHP + ")");

                damage = playerEntity.getHealth() - newHealth;
                subEvent.setDamage(damage);
                
                if (damage == 0) {
                    subEvent.setDamage(-1);
                    playerEntity.setHealth(newHealth);
                    //CraftPlayer craftPlayer = (CraftPlayer) playerEntity;
                    //craftPlayer.getHandle().netServerHandler.sendPacket(new Packet38EntityStatus(playerEntity.getEntityId(), (byte)2));
                }

            } else if (subEvent.getEntity() instanceof LivingEntity) {
                plugin.log(Level.INFO, "Recognized as livingentity!");

                LivingEntity livingEntity = (LivingEntity) subEvent.getEntity();
                CreatureType creatureType = prop.getCreatureFromEntity(livingEntity);

                if (!prop.mobMaxHealth.containsKey(creatureType)) {
                    return;
                }

                Double maxHealth = prop.mobMaxHealth.get(creatureType);

                if (!heroesDamage.getMobHealthValues().containsKey(livingEntity.getEntityId())) {
                    heroesDamage.getMobHealthValues().put(livingEntity.getEntityId(), maxHealth);
                }

                Integer entityMaxHp = 20;

                if (creatureType == CreatureType.GIANT) {
                    entityMaxHp = 50;
                } else if (creatureType == CreatureType.GHAST) {
                    entityMaxHp = 10;
                }

                heroesDamage.getMobHealthValues().put(livingEntity.getEntityId(), maxHealth - damage);

                Integer health = (int) ((livingEntity.getHealth() / entityMaxHp) * 20);

                if (livingEntity.getHealth() != health) {
                    subEvent.setDamage((int) (livingEntity.getHealth() - (health)));
                } else {
                    subEvent.setCancelled(true);
                }
            }
        }
    }
}
