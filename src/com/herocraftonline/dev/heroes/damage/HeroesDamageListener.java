package com.herocraftonline.dev.heroes.damage;

import java.util.logging.Level;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.Packet18ArmAnimation;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.damage.DamageManager.ProjectileType;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Properties;

public class HeroesDamageListener extends EntityListener {

    private static final boolean DEBUG = false;

    private Heroes plugin;
    private DamageManager damageManager;

    public HeroesDamageListener(Heroes plugin, DamageManager damageManager) {
        this.plugin = plugin;
        this.damageManager = damageManager;
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        CreatureType type = event.getCreatureType();
        Integer maxHealth = damageManager.getCreatureHealth(type);
        if (maxHealth != null) {
            entity.setHealth(maxHealth);
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (DEBUG) plugin.log(Level.INFO, "Damaged: " + event.getEntity().getClass().getSimpleName());

        DamageCause cause = event.getCause();
        int damage = event.getDamage();

        if (DEBUG) plugin.log(Level.INFO, "  Unmodified Event Damage: " + damage);

        if (event instanceof EntityDamageByEntityEvent) {
            if (DEBUG) plugin.log(Level.INFO, "  EDBE Event");
            if (event instanceof EntityDamageByProjectileEvent) {
                /*
                 * if (DEBUG) plugin.log(Level.INFO, "    EDBP Event");
                 * Projectile projectile = ((EntityDamageByProjectileEvent) event).getProjectile();
                 * ProjectileType type = ProjectileType.valueOf(projectile);
                 * if (DEBUG) plugin.log(Level.INFO, "      Projectile Type: " + type.name());
                 * Integer tmpDamage = damageManager.getProjectileDamage(type);
                 * if (DEBUG) plugin.log(Level.INFO, "      Projectile Damage: " + tmpDamage);
                 * if (tmpDamage != null) {
                 * damage = tmpDamage;
                 * }
                 */

            } else {
                Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
                if (attacker instanceof HumanEntity) {
                    if (DEBUG) plugin.log(Level.INFO, "    HumanEntity Attacker");
                    HumanEntity attackingHuman = (HumanEntity) attacker;
                    Material item = attackingHuman.getItemInHand().getType();
                    if (DEBUG) plugin.log(Level.INFO, "      Item: " + item.name());
                    Integer tmpDamage = damageManager.getItemDamage(item);
                    if (DEBUG) plugin.log(Level.INFO, "      Damage: " + tmpDamage);
                    if (tmpDamage != null) {
                        damage = tmpDamage;
                    }
                } else {
                    CreatureType type = Properties.getCreatureFromEntity(attacker);
                    if (type != null) {
                        if (DEBUG) plugin.log(Level.INFO, "    " + type.name() + " Attacker");
                        Integer tmpDamage = damageManager.getCreatureDamage(type);
                        if (DEBUG) plugin.log(Level.INFO, "      Damage: " + tmpDamage);
                        if (tmpDamage != null) {
                            damage = tmpDamage;
                        }
                    }
                }
            }
        } else if (cause != DamageCause.CUSTOM) {
            if (DEBUG) plugin.log(Level.INFO, "  Other Damage Cause");
            Integer tmpDamage = damageManager.getEnvironmentalDamage(cause);
            if (DEBUG) plugin.log(Level.INFO, "    Damage: " + tmpDamage);
            if (tmpDamage != null) {
                damage = tmpDamage;
                if (cause == DamageCause.FALL) {
                    damage += damage / 3 * (event.getDamage() - 3);
                }
            }
        }

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            onPlayerDamage(event, damage);
        } else if (entity instanceof LivingEntity) {
            event.setDamage(damage);
        }
    }

    private void onPlayerDamage(EntityDamageEvent event, int damage) {
        Player player = (Player) event.getEntity();
        Hero hero = plugin.getHeroManager().getHero(player);

        hero.setHealth(hero.getHealth() - damage);
        int visualDamage = (int) (player.getHealth() - hero.getHealth() / hero.getMaxHealth() * 20);
        event.setDamage(visualDamage);

        if (visualDamage == 0) {
            fakeDamageAnimation(player);
        }

    }

    private void fakeDamageAnimation(LivingEntity entity) {
        EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
        for (Player player : entity.getWorld().getPlayers()) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().netServerHandler.sendPacket(new Packet18ArmAnimation(nmsEntity, (byte) 2));
        }
    }
}
