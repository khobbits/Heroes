package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
/**
 * This is a cancellable event that fires whenever a hero changes from one class to another.
 * If this event is initiated because of an admin command the cancelled state will be ignored.
 */
public class ClassChangeEvent extends HeroEvent implements Cancellable {

    protected boolean cancelled = false;
    protected final Hero hero;
    protected final HeroClass from;
    protected HeroClass to;

    public ClassChangeEvent(Hero hero, HeroClass from, HeroClass to) {
        super("ClassChangeEvent", HeroEventType.HERO_CLASS_CHANGE);
        this.hero = hero;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the class the Hero is changing from
     * @return
     */
    public final HeroClass getFrom() {
        return from;
    }

    /**
     * Returns the hero that is changing classes
     * @return
     */
    public final Hero getHero() {
        return hero;
    }

    /**
     * Returns the class the hero is changing to
     * @return
     */
    public HeroClass getTo() {
        return to;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
