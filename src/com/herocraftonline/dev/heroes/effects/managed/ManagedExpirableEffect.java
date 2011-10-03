package com.herocraftonline.dev.heroes.effects.managed;

import com.herocraftonline.dev.heroes.effects.ExpirableEffect;

public class ManagedExpirableEffect {
    
    public final ExpirableEffect effect;
    
    protected ManagedExpirableEffect(ExpirableEffect effect) {
        this.effect = effect;
    }
}
