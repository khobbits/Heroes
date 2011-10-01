package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
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

    public final HeroClass getFrom() {
        return from;
    }

    public final Hero getHero() {
        return hero;
    }

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
