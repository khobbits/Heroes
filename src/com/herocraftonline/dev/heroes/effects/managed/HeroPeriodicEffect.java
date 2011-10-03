package com.herocraftonline.dev.heroes.effects.managed;

import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.hero.Hero;

public class HeroPeriodicEffect extends ManagedPeriodicEffect {
    
    public final Hero hero;
    
    public HeroPeriodicEffect(Hero hero, PeriodicEffect effect) {
        super(effect);
        this.hero = hero;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 7;
        result = prime * result + effect.hashCode();
        result = prime * result + hero.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HeroPeriodicEffect other = (HeroPeriodicEffect) obj;
        if (!effect.equals(other.effect))
            return false;
        else if (!hero.equals(other.hero))
            return false;
        return true;
    }
}
