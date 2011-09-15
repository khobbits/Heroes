package com.herocraftonline.dev.heroes.effects;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class Effect {

    protected final String name;
    protected final Skill skill;
    protected final Heroes plugin;
    protected final Set<EffectType> types = EnumSet.noneOf(EffectType.class);
    protected long applyTime;
    private boolean persistent;

    public Effect(Skill skill, String name) {
        this.name = name;
        this.skill = skill;
        if (skill != null) {
            this.plugin = skill.plugin;
        } else {
            this.plugin = null;
        }
        this.persistent = false;
    }

    public Effect(Skill skill, String name, EffectType... types) {
        this(skill, name);
        for (EffectType type : types) {
            this.types.add(type);
        }
    }

    public void apply(Hero hero) {
        this.applyTime = System.currentTimeMillis();
    }

    public void apply(Creature creature) {
        this.applyTime = System.currentTimeMillis();
    }

    public void broadcast(Location source, String message, Object... args) {
        skill.broadcast(source, message, args);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Effect other = (Effect) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return the Skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Returns if this Effect is the type specified
     * 
     * @param type
     * @return
     */
    public boolean isType(EffectType type) {
        return types.contains(type);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    /**
     * Returns if the effect is persistent or not
     * 
     * @return
     */
    public boolean isPersistent() {
        return persistent;
    }

    public void remove(Hero hero) {}

    public void remove(Creature creature) {}

    /*
     * Sets the effects persistence value
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

}
