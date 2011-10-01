package com.herocraftonline.dev.heroes.api;

import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
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

    public final int getFrom() {
        return from;
    }

    public Hero getHero() {
        return hero;
    }

    public final int getTo() {
        return to;
    }

}
