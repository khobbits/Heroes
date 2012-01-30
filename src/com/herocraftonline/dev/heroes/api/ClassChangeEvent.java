package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
/**
 * This is a cancellable event that fires whenever a hero changes from one class to another.
 * If this event is initiated because of an admin command the cancelled state will be ignored.
 */
public class ClassChangeEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;
    protected final Hero hero;
    protected final HeroClass from;
    protected HeroClass to;

    public ClassChangeEvent(Hero hero, HeroClass from, HeroClass to) {
        super("ClassChangeEvent");
        this.hero = hero;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the class the Hero is changing from
     * @return HeroClass - previous HeroClass
     */
    public final HeroClass getFrom() {
        return from;
    }

    /**
     * Returns the hero that is changing classes
     * @return hero that is changing classes
     */
    public final Hero getHero() {
        return hero;
    }

    /**
     * Returns the class the hero is changing to
     * @return HeroClass the hero is changing to
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
