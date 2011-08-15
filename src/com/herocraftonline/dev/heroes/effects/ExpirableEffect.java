package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class ExpirableEffect extends Effect implements Expirable {

    private final long duration;
    private long applyTime;

    public ExpirableEffect(Skill skill, String name, long duration) {
        super(skill, name);
        this.duration = duration;
    }

    @Override
    public void apply(Hero hero) {
        applyTime = System.currentTimeMillis();
    }

    @Override
    public void apply(Creature creature) {
        applyTime = System.currentTimeMillis();
    }
    
    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public long getExpiry() {
        return applyTime + duration;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() >= getExpiry();
    }

    @Override
    public void remove(Hero hero) {

    }
    
    @Override
    public void remove(Creature creature) {
        
    }
}
