package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

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

    public Entity getAttacker() {
        return attacker;
    }

    /**
     * Supports null value if the attacker is not a player
     * 
     * @return
     */
    public ItemStack getWeapon() {
        return weapon == null ? null : weapon.clone();
    }
}
