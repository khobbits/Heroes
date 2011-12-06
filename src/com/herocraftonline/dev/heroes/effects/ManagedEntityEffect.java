package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.LivingEntity;

public class ManagedEntityEffect extends ManagedEffect {
    
    public final LivingEntity lEntity;

    
    public ManagedEntityEffect(LivingEntity lEntity, Effect effect) {
        super(effect);
        this.lEntity = lEntity;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 7;
        result = prime * result + effect.hashCode();
        result = prime * result + lEntity.hashCode();
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
        ManagedEntityEffect other = (ManagedEntityEffect) obj;
        if (!effect.equals(other.effect))
            return false;
        else if (!lEntity.equals(other.lEntity))
            return false;
        return true;
    }
}