package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class ExpirableEffect extends Effect implements Expirable {

    private final long duration;
    private long expireTime;

    public ExpirableEffect(Skill skill, String name, long duration) {
        super(skill, name);
        this.duration = duration;
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        this.expireTime = applyTime + duration;
    }

    @Override
    public void apply(Creature creature) {
        super.apply(creature);
        this.expireTime = applyTime + duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public long getApplyTime() {
        return applyTime;
    }

    @Override
    public long getExpiry() {
        return expireTime;
    }

    @Override
    public boolean isExpired() {
        if (isPersistent())
            return false;
        return System.currentTimeMillis() >= getExpiry();
    }

    @Override
    public void remove(Hero hero) {

    }

    @Override
    public void remove(Creature creature) {

    }

    @Override
    public long getRemainingTime() {
        return expireTime - System.currentTimeMillis();
    }
}
