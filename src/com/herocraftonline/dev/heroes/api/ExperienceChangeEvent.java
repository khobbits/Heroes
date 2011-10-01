package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;

import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;

@SuppressWarnings("serial")
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

    public final Hero getHero() {
        return hero;
    }

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
     * Sets the player's experience
     * 
     * @param exp
     */
    public void setExpGain(double exp) {
        this.expChange = exp;
    }

}
