package com.herocraftonline.dev.heroes.effects.managed;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.effects.PeriodicEffect;

public class CreaturePeriodicEffect extends ManagedPeriodicEffect {
    
    public final Creature creature;

    
    public CreaturePeriodicEffect(Creature creature, PeriodicEffect effect) {
        super(effect);
        this.creature = creature;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 7;
        result = prime * result + effect.hashCode();
        result = prime * result + creature.hashCode();
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
        CreaturePeriodicEffect other = (CreaturePeriodicEffect) obj;
        if (!effect.equals(other.effect))
            return false;
        else if (!creature.equals(other.creature))
            return false;
        return true;
    }
}
