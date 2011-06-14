package com.herocraftonline.dev.heroes.classes;

import java.util.Set;

import com.herocraftonline.dev.heroes.Heroes;

public class HeroClassManager {
    private Heroes plugin;

    protected HeroClassManager(Heroes plugin) {
        this.plugin = plugin;
    }

    public Set<HeroClass> getHeroClasses() {
        return plugin.getClassManager().getClasses();
    }

    public void setDefaultClass(HeroClass defaultClass) {
        plugin.getClassManager().setDefaultClass(defaultClass);
    }

    public HeroClass getDefaultClass() {
        return plugin.getClassManager().getDefaultClass();
    }

    public HeroClass getClass(String name) {
        return plugin.getClassManager().getClass(name);
    }

    public void removeClass(HeroClass c) {
        plugin.getClassManager().removeClass(c);
    }

    public void addClass(HeroClass c) {
        plugin.getClassManager().addClass(c);
    }
}
