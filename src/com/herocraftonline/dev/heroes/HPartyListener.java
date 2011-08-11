package com.herocraftonline.dev.heroes;

import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HPartyListener extends EntityListener {

    private Heroes plugin;

    public HPartyListener(Heroes plugin) {
        this.plugin = plugin;
    }

    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();
        Hero hero = this.plugin.getHeroManager().getHero(player);

        if (!hero.hasParty()) {
            return;
        }
        HeroParty party = hero.getParty();
        if (event.getDamage() > 0 && !party.updateMapDisplay()) {
            party.setUpdateMapDisplay(true);
        }
    }

    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Hero hero = this.plugin.getHeroManager().getHero(player);

        if (!hero.hasParty()) {
            return;
        }
        HeroParty party = hero.getParty();
        if (event.getAmount() > 0 && !party.updateMapDisplay()) {
            party.setUpdateMapDisplay(true);
        }
    }
}
