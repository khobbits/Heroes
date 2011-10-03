package com.herocraftonline.dev.heroes.effects.managed;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.effects.Expirable;

public class CreatureExpirableEffect extends ManagedExpirableEffect {
    
    public final Creature creature;

    
    public CreatureExpirableEffect(Creature creature, Expirable effect) {
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
        CreatureExpirableEffect other = (CreatureExpirableEffect) obj;
        if (!effect.equals(other.effect))
            return false;
        else if (!creature.equals(other.creature))
            return false;
        return true;
    }
}
