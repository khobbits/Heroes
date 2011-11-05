package com.herocraftonline.dev.heroes;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;

public class HPartyListener extends EntityListener {

    private Heroes plugin;

    public HPartyListener(Heroes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Hero hero = this.plugin.getHeroManager().getHero(player);

        if (!hero.hasParty())
            return;
        HeroParty party = hero.getParty();
        if (event.getAmount() > 0) {
            party.update();
        }
    }
}
