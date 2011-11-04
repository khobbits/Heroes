package com.herocraftonline.dev.heroes.api;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;

/**
 * This event is called when a hero leaves a party.
 */
@SuppressWarnings("serial")
public class HeroLeavePartyEvent extends HeroEvent {

    private final Hero hero;
    private final HeroParty party;

    public HeroLeavePartyEvent(Hero hero, HeroParty heroParty) {
        super("HeroLeavePartyEvent", HeroEventType.HERO_LEAVE_PARTY);
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
