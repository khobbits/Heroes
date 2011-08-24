package com.herocraftonline.dev.heroes.api;

import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.persistence.Hero;

@SuppressWarnings("serial")
public class HeroLevelEvent extends ExperienceChangeEvent {

    protected final int from;
    protected final int to;

    public HeroLevelEvent(Hero hero, double expChange, int from, int to, ExperienceType source) {
        super(hero, expChange, source);
        this.expChange = expChange;
        this.from = from;
        this.to = to;
    }

    public final int getFrom() {
        return from;
    }

    public final int getTo() {
        return to;
    }

}
