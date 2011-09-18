package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicEffect extends Effect implements Periodic {

    private final long period;
    protected long lastTickTime;

    public PeriodicEffect(Skill skill, String name, long period) {
        super(skill, name);
        this.period = period;
    }
    
    @Override
    public long getPeriod() {
        return period;
    }

    public long getLastTickTime() {
        return lastTickTime;
    }

    @Override
    public boolean isReady() {
        return System.currentTimeMillis() >= lastTickTime + period;
    }

    @Override
    public void remove(Hero hero) {

    }

    @Override
    public void remove(Creature creature) {

    }

    @Override
    public void tick(Hero hero) {
        lastTickTime = System.currentTimeMillis();
    }

    @Override
    public void tick(Creature creature) {
        lastTickTime = System.currentTimeMillis();
    }

    /**
     * @return the applyTime
     */
    public long getApplyTime() {
        return applyTime;
    }

}
