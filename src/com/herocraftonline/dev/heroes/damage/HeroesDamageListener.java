package com.herocraftonline.dev.heroes.damage;

import java.util.logging.Level;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet8UpdateHealth;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Properties;

public class HeroesDamageListener extends EntityListener {

    public Heroes plugin;
    public DamageManager damageManager;

    public HeroesDamageListener(Heroes plugin, DamageManager damageManager) {
        this.plugin = plugin;
        this.damageManager = damageManager;
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        CreatureType type = event.getCreatureType();
        int maxHealth = damageManager.getCreatureHealth(type);
        entity.setHealth(maxHealth);
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

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
            Material item = damager.getItemInHand().getType();
            int damage = damageManager.getItemDamage(item);

            if (subEvent.getEntity() instanceof Player) {
                Player playerEntity = (Player) subEvent.getEntity();
                Hero heroEntity = plugin.getHeroManager().getHero(playerEntity);

                int visualDamage = DamageManager.getVisualDamage(heroEntity, damage);
                subEvent.setDamage(visualDamage);

                EntityPlayer defenderEntityPlayer = ((CraftPlayer) playerEntity).getHandle();
                if (visualDamage == 0) {
                    for (Player player : playerEntity.getWorld().getPlayers()) {
                        CraftPlayer craftPlayer = (CraftPlayer) player;
                        craftPlayer.getHandle().netServerHandler.sendPacket(new Packet18ArmAnimation(defenderEntityPlayer, (byte) 2));
                    }
                }
            } else if (subEvent.getEntity() instanceof LivingEntity) {
                subEvent.setDamage(damage);
            }
        }
    }
}
