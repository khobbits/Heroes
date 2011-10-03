package com.herocraftonline.dev.heroes.effects.managed;

import com.herocraftonline.dev.heroes.effects.PeriodicEffect;

public class ManagedPeriodicEffect {
    
    public final PeriodicEffect effect;
    
    protected ManagedPeriodicEffect(PeriodicEffect effect) {
        this.effect = effect;
    }
}
