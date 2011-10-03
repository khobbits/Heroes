package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@SuppressWarnings("serial")
/**
 * This event is called when a DamageCause.ATTACK event is registered in the heroesdamagelistener
 * Both creatures & Players can cause this event to be fired even if they aren't using a 'weapon' per se.
 * This event is cancellable.
 */
public class WeaponDamageEvent extends HeroEvent implements Cancellable {

    private int damage;
    private final Entity damager;
    private final Entity entity;
    private final DamageCause cause;
    private boolean cancelled = false;

    public WeaponDamageEvent(int damage, EntityDamageByEntityEvent event) {
        super("HeroesWeaponDamageEvent", HeroEventType.WEAPON_DAMAGE);
        this.damage = damage;
        this.damager = event.getDamager();
        this.entity = event.getEntity();
        this.cause = event.getCause();
    }
    
    /**
     * @return the DamageCause
     */
    public DamageCause getCause() {
        return cause;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the entity dealing the damage
     */
    public Entity getDamager() {
        return damager;
    }

    /**
     * @return the entity being damaged
     */
    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean val) {
        this.cancelled = val;
    }

    /**
     * @param damage
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }
}
