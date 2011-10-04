package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicExpirableEffect extends ExpirableEffect implements Periodic {

    private final long period;
    protected long lastTickTime;

    public PeriodicExpirableEffect(Skill skill, String name, long period, long duration) {
        super(skill, name, duration);
        this.period = period;
    }

    @Override
    public long getLastTickTime() {
        return lastTickTime;
    }

    @Override
    public long getPeriod() {
        return period;
    }

    @Override
    public boolean isReady() {
        return System.currentTimeMillis() >= lastTickTime + period;
    }

    @Override
    public void remove(Creature creature) {
        super.remove(creature);
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
    }

    @Override
    public void tick(Creature creature) {
        lastTickTime = System.currentTimeMillis();
    }

    @Override
    public void tick(Hero hero) {
        lastTickTime = System.currentTimeMillis();
    }

}
