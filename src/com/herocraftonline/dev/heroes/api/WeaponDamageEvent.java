package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@SuppressWarnings("serial")
public class WeaponDamageEvent extends Event implements Cancellable {

    private int damage;
    private Entity damager;
    private Entity entity;
    private DamageCause cause;
    private boolean cancelled = false;

    public WeaponDamageEvent(int damage, EntityDamageByEntityEvent event) {
        super("HeroesWeaponDamageEvent");
        this.damage = damage;
        this.damager = event.getDamager();
        this.entity = event.getEntity();
        this.cause = event.getCause();
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setCause(DamageCause cause) {
        this.cause = cause;
    }

    public DamageCause getCause() {
        return cause;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setDamager(Entity damager) {
        this.damager = damager;
    }

    public Entity getDamager() {
        return damager;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean val) {
        this.cancelled = val;
    }

}
