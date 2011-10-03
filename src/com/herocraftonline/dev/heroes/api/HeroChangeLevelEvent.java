package com.herocraftonline.dev.heroes.api;

import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
/**
 * Called when a Hero changes levels, either through admin commands or when Experience adjusts their level higher/lower..\
 * Data during this event is unable to be changed.
 */
public class HeroChangeLevelEvent extends HeroEvent {

    private final int from;
    private final int to;
    private final Hero hero;

    public HeroChangeLevelEvent(Hero hero, int from, int to) {
        super("HeroLevelEvent", HeroEventType.HERO_LEVEL_CHANGE);
        this.from = from;
        this.to = to;
        this.hero = hero;
    }

    /**
     * The level the hero is changing from
     * @return
     */
    public final int getFrom() {
        return from;
    }

    /**
     * Returns the hero being adjusted
     * @return
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Returns the level the hero will be after the event
     * @return
     */
    public final int getTo() {
        return to;
    }

}
