package com.herocraftonline.dev.heroes.party;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.HeroManager;

public class PartyEntityListener extends EntityListener {

    public Heroes plugin;

    public PartyEntityListener(Heroes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent initialEvent) {
        if (initialEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) initialEvent;
            if (subEvent.getEntity() instanceof Player && subEvent.getDamager() instanceof Player) {
                Player attacker = (Player) subEvent.getDamager();
                Player defender = (Player) subEvent.getEntity();
                HeroManager heroManager = plugin.getHeroManager();
                HeroParty attackParty = heroManager.getHero(attacker).getParty();

                if (attackParty == null) {
                    return;
                }
                if (attackParty.isPartyMember(heroManager.getHero(defender)) && attackParty.getPvp()) {
                    initialEvent.setCancelled(true);
                }
            }
        }
    }
}
