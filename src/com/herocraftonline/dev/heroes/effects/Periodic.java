package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.persistence.Hero;

public interface Periodic {

    public long getPeriod();

    public boolean isReady();

    public void tick(Hero hero);

    public void tick(Creature creature);
}
