package com.herocraftonline.dev.heroes.damage;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

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
        Material item = damager.getItemInHand().getType();
        int damage = damageManager.getItemDamage(item);

        if (subEvent.getEntity() instanceof Player) {
            Player playerEntity = (Player) subEvent.getEntity();
            Hero heroEntity = plugin.getHeroManager().getHero(playerEntity);

            int visualDamage = DamageManager.getVisualDamage(heroEntity, damage);
            subEvent.setDamage(visualDamage);

            if (visualDamage == 0) {
                fakeDamageAnimation(playerEntity);
            }
        } else if (subEvent.getEntity() instanceof LivingEntity) {
            subEvent.setDamage(damage);
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
