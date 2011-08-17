package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;

@SuppressWarnings("serial")
public class HeroLeavePartyEvent extends Event {

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

}
