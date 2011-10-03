package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;

import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
/**
 * This event is fired whenever a hero gains or loses experience.  It is a cancellable event, but
 * if it is called via an admin command the cancelled state will be ignored.
 */
public class ExperienceChangeEvent extends HeroEvent implements Cancellable {

    protected boolean cancelled = false;
    protected final Hero hero;
    protected double expChange;
    protected final ExperienceType source;

    public ExperienceChangeEvent(Hero hero, double expChange, ExperienceType source) {
        super("ExperienceGainEvent", HeroEventType.HERO_EXPERIENCE_CHANGE);
        this.hero = hero;
        this.expChange = expChange;
        this.source = source;
    }

    /**
     * Returns the player's experience
     * 
     * @return
     */
    public double getExpChange() {
        return expChange;
    }

    /**
     * Returns the hero
     * @return
     */
    public final Hero getHero() {
        return hero;
    }

    /**
     * Returns the ExperienceType source
     * @return
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

}
