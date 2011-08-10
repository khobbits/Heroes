package com.herocraftonline.dev.heroes.party;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;

public class PartyEntityListener extends EntityListener {

    public Heroes plugin;

    public PartyEntityListener(Heroes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent initialEvent) {
        if (initialEvent.isCancelled()) return;
        
        if (initialEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) initialEvent;
            if (subEvent.getEntity() instanceof Player) {
                Player defender = (Player) subEvent.getEntity();
                Player attacker = null;
                if (subEvent.getDamager() instanceof Player) {
                    attacker = (Player) subEvent.getDamager();
                } else if (subEvent.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) subEvent.getDamager();
                    if (projectile.getShooter() instanceof Player) {
                        attacker = (Player) projectile.getShooter();
                    }
                }
                if (attacker == null) return;
                HeroParty party = plugin.getHeroManager().getHero(defender).getParty();
                if (party == null) return;
                if (party.isPartyMember(plugin.getHeroManager().getHero(attacker))) {
                    plugin.debugLog(Level.INFO, "Party damage done to: " + defender.getName() + " by: " + attacker.getName() + " has been cancelled.");
                    initialEvent.setCancelled(true);
                }
            }
        }
    }
}
