package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;

@SuppressWarnings("serial")
public class HeroJoinPartyEvent extends HeroEvent implements Cancellable {

    private boolean cancelled = false;
    private final Hero hero;
    private final HeroParty party;

    public HeroJoinPartyEvent(Hero hero, HeroParty heroParty) {
        super("PlayerJoinPartyEvent", HeroEventType.HERO_JOIN_PARTY);
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
