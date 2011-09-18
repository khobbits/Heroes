package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;

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
