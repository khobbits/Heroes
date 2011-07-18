package com.herocraftonline.dev.heroes.effects;

import com.herocraftonline.dev.heroes.persistence.Hero;

public interface Periodic {

    public long getPeriod();

    public boolean isReady();

    public void tick(Hero hero);

}
