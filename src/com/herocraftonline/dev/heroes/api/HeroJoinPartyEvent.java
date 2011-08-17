package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;

@SuppressWarnings("serial")
public class HeroJoinPartyEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private final Hero hero;
    private final HeroParty party;
    
    public HeroJoinPartyEvent(Hero hero, HeroParty heroParty) {
        super("PlayerJoinPartyEvent");
        this.hero = hero;
        this.party = heroParty;
    }

    /**
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * @return the heroParty
     */
    public HeroParty getParty() {
        return party;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean val) {
        this.cancelled = val;
    }

}
