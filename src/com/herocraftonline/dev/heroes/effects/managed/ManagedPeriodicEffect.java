package com.herocraftonline.dev.heroes.effects.managed;

import com.herocraftonline.dev.heroes.effects.Periodic;

public class ManagedPeriodicEffect {
    
    public final Periodic effect;
    
    protected ManagedPeriodicEffect(Periodic effect) {
        this.effect = effect;
    }
}
