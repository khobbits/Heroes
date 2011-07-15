package com.herocraftonline.dev.heroes.classes;

import java.util.Set;

import com.herocraftonline.dev.heroes.Heroes;

public class HeroClassManager {
    private Heroes plugin;

    protected HeroClassManager(Heroes plugin) {
        this.plugin = plugin;
    }

    public void addClass(HeroClass c) {
        plugin.getClassManager().addClass(c);
    }

    public HeroClass getClass(String name) {
        return plugin.getClassManager().getClass(name);
    }

    public HeroClass getDefaultClass() {
        return plugin.getClassManager().getDefaultClass();
    }

    public Set<HeroClass> getHeroClasses() {
        return plugin.getClassManager().getClasses();
    }

    public void removeClass(HeroClass c) {
        plugin.getClassManager().removeClass(c);
    }

    public void setDefaultClass(HeroClass defaultClass) {
        plugin.getClassManager().setDefaultClass(defaultClass);
    }
}
