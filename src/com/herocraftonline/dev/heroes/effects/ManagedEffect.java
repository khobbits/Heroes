package com.herocraftonline.dev.heroes.effects;

public class ManagedEffect {
    
    public final Effect effect;
    
    protected ManagedEffect(Effect effect) {
        this.effect = effect;
    }
    
    public Effect getEffect() {
        return effect;
    }
}