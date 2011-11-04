package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a hero taking damage from an attack - (not a skill)
 * @author sleak
 *
 */
public class HeroAttackDamageCause extends HeroDamageCause {

    private ItemStack weapon = null;
    private final Entity attacker;

    public HeroAttackDamageCause(int damage, DamageCause cause, Entity attacker) {
        super(damage, cause);
        this.attacker = attacker;
        if (attacker instanceof Player) {
            weapon = ((Player) attacker).getItemInHand();
        }
    }

    /**
     * Returns the entity attacker
     * @return
     */
    public Entity getAttacker() {
        return attacker;
    }

    /**
     * Supports null value if the attacker is not a player
     * Will return an ItemStack of the weapon being used to damage
     * @return
     */
    public ItemStack getWeapon() {
        return weapon == null ? null : weapon.clone();
    }
}
