package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class ExpirableEffect extends Effect implements Expirable {

    private final long duration;
    private long expireTime;

    public ExpirableEffect(Skill skill, String name, long duration) {
        super(skill, name);
        this.duration = duration;
    }

    @Override
    public void apply(LivingEntity lEntity) {
        super.apply(lEntity);
        this.expireTime = applyTime + duration;
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        this.expireTime = applyTime + duration;
    }

    public long getApplyTime() {
        return applyTime;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public long getExpiry() {
        return expireTime;
    }

    @Override
    public long getRemainingTime() {
        return expireTime - System.currentTimeMillis();
    }

    @Override
    public boolean isExpired() {
        if (isPersistent())
            return false;
        return System.currentTimeMillis() >= getExpiry();
    }
    
    @Override
    public void expire() {
        this.expireTime = System.currentTimeMillis();
    }

    @Override
    public void remove(LivingEntity lEntity) {
        super.remove(lEntity);
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
    }
}
