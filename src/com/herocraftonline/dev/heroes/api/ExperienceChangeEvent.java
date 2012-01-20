package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;

/**
 * This event is fired whenever a hero gains or loses experience.  It is a cancellable event, but
 * if it is called via an admin command the cancelled state will be ignored.
 */
@SuppressWarnings("serial")
public class ExperienceChangeEvent extends HeroEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;
    protected final Hero hero;
    protected final HeroClass heroClass;
    protected double expChange;
    protected final ExperienceType source;

    public ExperienceChangeEvent(Hero hero, HeroClass heroClass, double expChange, ExperienceType source) {
        super("ExperienceGainEvent", HeroEventType.HERO_EXPERIENCE_CHANGE);
        this.hero = hero;
        this.expChange = expChange;
        this.source = source;
        this.heroClass = heroClass;
    }

    /**
     * Returns the player's experience change
     * Can be either positive or negative
     * 
     * @return the amount of experience being changed
     */
    public double getExpChange() {
        return expChange;
    }

    /**
     * @return the Hero
     */
    public final Hero getHero() {
        return hero;
    }

    /**
     * Returns the ExperienceType source
     * @return the ExperienceType source
     */
    public final ExperienceType getSource() {
        return source;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Sets the amount of experience being added or removed
     * supports negative values
     * @param exp
     */
    public void setExpGain(double exp) {
        this.expChange = exp;
    }
    
    /**
     * The class gaining the xp
     * @return HeroClass gaining the xp
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
