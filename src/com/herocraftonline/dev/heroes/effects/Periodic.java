package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.hero.Hero;

public interface Periodic {

    /**
     * @return the period
     */
    public long getPeriod();

    /**
     * Returns whether the effect is ready for ticking
     * @return
     */
    public boolean isReady();

    /**
     * runs the effect on the specified hero
     * 
     * @param hero
     */
    public void tick(Hero hero);

    /**
     * runs the effect on the specified creature
     * 
     * @param creature
     */
    public void tick(Creature creature);
    
    /**
     * Returns the last time the effect ticked
     * 
     * @return
     */
    public long getLastTickTime();
}
