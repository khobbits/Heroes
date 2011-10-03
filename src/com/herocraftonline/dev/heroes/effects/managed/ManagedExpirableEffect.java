package com.herocraftonline.dev.heroes.effects.managed;

import com.herocraftonline.dev.heroes.effects.Expirable;

public class ManagedExpirableEffect {
    
    public final Expirable effect;
    
    protected ManagedExpirableEffect(Expirable effect) {
        this.effect = effect;
    }
}
