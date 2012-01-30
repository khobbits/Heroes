package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;

/**
 * This event is called when a hero leaves a party.
 */
@SuppressWarnings("serial")
public class HeroLeavePartyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Hero hero;
    private final HeroParty party;

    public HeroLeavePartyEvent(Hero hero, HeroParty heroParty) {
        super("HeroLeavePartyEvent");
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
     * @return the party
     */
    public HeroParty getParty() {
        return party;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
