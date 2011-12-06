package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicEffect extends Effect implements Periodic {

    private final long period;
    protected long lastTickTime = 0;

    public PeriodicEffect(Skill skill, String name, long period) {
        super(skill, name);
        this.period = period;
    }

    /**
     * @return the applyTime
     */
    public long getApplyTime() {
        return applyTime;
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
    public void remove(LivingEntity lEntity) {
        super.remove(lEntity);
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
    }

    @Override
    public void tick(LivingEntity lEntity) {
        lastTickTime = System.currentTimeMillis();
    }

    @Override
    public void tick(Hero hero) {
        lastTickTime = System.currentTimeMillis();
    }

}
