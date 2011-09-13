package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicEffect extends ExpirableEffect implements Periodic {

    private final long period;
    protected long lastTickTime;

    public PeriodicEffect(Skill skill, String name, long period, long duration) {
        super(skill, name, duration);
        this.period = period;
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
    }

    @Override
    public void apply(Creature creature) {
        super.apply(creature);
    }

    @Override
    public long getPeriod() {
        return period;
    }

    /**
     * @return the lastTickTime
     */
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

}
