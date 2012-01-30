package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;


/**
 * Called when a Hero changes levels, either through admin commands or when Experience adjusts their level higher/lower..\
 * Data during this event is unable to be changed.
 */
@SuppressWarnings("serial")
public class HeroChangeLevelEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final int from;
    private final int to;
    private final Hero hero;
    private final HeroClass heroClass;

    public HeroChangeLevelEvent(Hero hero, HeroClass heroClass, int from, int to) {
        super("HeroLevelEvent");
        this.heroClass = heroClass;
        this.from = from;
        this.to = to;
        this.hero = hero;
    }

    /**
     * @return The level the hero is changing from
     */
    public final int getFrom() {
        return from;
    }

    /**
     * @return Returns the hero being adjusted
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Returns the level the hero will be after the event
     * @return the level the hero is attaining
     */
    public final int getTo() {
        return to;
    }

    /**
     * Returns the class gaining the level
     * @return the HeroClass gaining the level(s)
     */
    public HeroClass getHeroClass() {
        return heroClass;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
