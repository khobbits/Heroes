package com.herocraftonline.dev.heroes.effects;

import org.bukkit.Location;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class Effect {

    private final String name;
    private final Skill skill;
    private boolean persistent;

    public Effect(Skill skill, String name) {
        this.name = name;
        this.skill = skill;
        this.persistent = false;
    }

    public String getName() {
        return name;
    }

    public Skill getSkill() {
        return skill;
    }

    public boolean isPersistent() {
        return persistent;
    }
    
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public void broadcast(Location source, String message, Object... args) {
        skill.broadcast(source, message, args);
    }

    public void apply(Hero hero) {}

    public void remove(Hero hero) {}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Effect other = (Effect) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

}
