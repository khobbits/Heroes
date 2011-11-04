package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Represents a reason for the Hero to be taking damage and the amount of damage they are taking
 * @author sleak
 *
 */
public class HeroDamageCause {

    private final DamageCause cause;
    private final int damage;

    public HeroDamageCause(int damage, DamageCause cause) {
        this.damage = damage;
        this.cause = cause;
    }

    /**
     * @return the cause
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
}
