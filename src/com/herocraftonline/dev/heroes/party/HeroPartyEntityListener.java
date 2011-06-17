package com.herocraftonline.dev.heroes.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class HeroPartyEntityListener extends EntityListener {

    private Heroes plugin;

    public HeroPartyEntityListener(Heroes plugin) {
        this.plugin = plugin;
    }

    // Handles PVP
    public void onEntityDamage(EntityDamageEvent e) {
        EntityDamageByEntityEvent event = null;
        if (e instanceof EntityDamageByEntityEvent) {
            event = (EntityDamageByEntityEvent) e;

        } else {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entityPlayer = (Player) event.getEntity();
            Player damagerPlayer = (Player) event.getDamager();
            Hero entityHero = plugin.getHeroManager().getHero(entityPlayer);
            Hero damagerHero = plugin.getHeroManager().getHero(damagerPlayer);
            if (entityHero.getParty() != null && entityHero.getParty().isPartyMember(damagerPlayer) && entityHero.getParty().checkMode("PVP")) {
                event.setCancelled(true);
            }
        }
    }
}
