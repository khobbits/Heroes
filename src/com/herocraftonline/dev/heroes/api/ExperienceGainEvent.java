package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.persistence.Hero;

@SuppressWarnings("serial")
public class ExperienceGainEvent extends Event implements Cancellable {

    protected boolean cancelled = false;
    protected final Hero hero;
    protected double expGain;
    protected final ExperienceType source;

    public ExperienceGainEvent(Hero hero, double expGain, ExperienceType source) {
        super("ExperienceGainEvent");
        this.hero = hero;
        this.expGain = expGain;
        this.source = source;
    }

    public final Hero getHero() {
        return hero;
    }

    public final ExperienceType getSource() {
        return source;
    }

    /**
     * Returns the player's experience
     * 
     * @return
     */
    public double getExpGain() {
        return expGain;
    }

    /**
     * Sets the player's experience
     * 
     * @param exp
     */
    public void setExpGain(double exp) {
        this.expGain = exp;
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
