package com.herocraftonline.dev.heroes.damage;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Properties;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

// import org.bukkit.entity.Projectile;
// import com.herocraftonline.dev.heroes.damage.DamageManager.ProjectileType;

public class HeroesDamageListener extends EntityListener {

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
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();
        int amount = event.getAmount();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            Hero hero = plugin.getHeroManager().getHero(player);
            double newHeroHealth = hero.getHealth() + amount;
            int newHealth = (int) (newHeroHealth / hero.getMaxHealth() * 20);
            int newAmount = newHealth - player.getHealth();
            hero.setHealth(newHeroHealth);
            event.setAmount(newAmount);
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();
        DamageCause cause = event.getCause();
        int damage = event.getDamage();

        if (event instanceof EntityDamageByEntityEvent) {
            if (event instanceof EntityDamageByProjectileEvent) {
                //                Projectile projectile = ((EntityDamageByProjectileEvent) event).getProjectile();
                //                DamageManager.ProjectileType type = DamageManager.ProjectileType.valueOf(projectile);
                //                Integer tmpDamage = damageManager.getProjectileDamage(type, (HumanEntity) projectile.getShooter());
                //                if (tmpDamage != null) {
                //                    damage = tmpDamage;
                //                }
            } else {
                Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
                if (attacker instanceof Player) {
                    Player attackingPlayer = (Player) attacker;
                    ItemStack weapon = attackingPlayer.getItemInHand();
                    Material weaponType = weapon.getType();

                    //                    if (entity instanceof Player) {
                    //                        if (weaponType.getMaxDurability() > 0) {
                    //                            EntityPlayer entityPlayer = ((CraftPlayer) attackingPlayer).getHandle();
                    //                            if (weaponType.getMaxDurability() + weapon.getDurability() > 0) {
                    //                                entityPlayer.inventory.getItemInHand().damage(1, entityPlayer);
                    //                            } else {
                    //                                entityPlayer.inventory.setItem(entityPlayer.inventory.itemInHandIndex, null);
                    //                                //attackingPlayer.setItemInHand(null);
                    //                            }
                    //                        }
                    //                    }

                    Integer tmpDamage = damageManager.getItemDamage(weaponType, attackingPlayer);
                    if (tmpDamage != null) {
                        damage = tmpDamage;
                    }
                } else {
                    CreatureType type = Properties.getCreatureFromEntity(attacker);
                    if (type != null) {
                        Integer tmpDamage = damageManager.getCreatureDamage(type);
                        if (tmpDamage != null) {
                            damage = tmpDamage;
                        }
                    }
                }
            }
        } else if (cause != DamageCause.CUSTOM) {
            Integer tmpDamage = damageManager.getEnvironmentalDamage(cause);
            if (tmpDamage != null) {
                damage = tmpDamage;
                if (cause == DamageCause.FALL) {
                    damage += damage / 3 * (event.getDamage() - 3);
                }
            }
        }

        System.out.println(damage);

        if (entity instanceof Player) {
            event.setDamage(damage);
            //plugin.getHeroManager().getHero((Player) entity).damage(damage);
            //event.setCancelled(true);
        } else if (entity instanceof LivingEntity) {
            event.setDamage(damage);
        }
    }
}
