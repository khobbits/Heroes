package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Event;

@SuppressWarnings("serial")
public class HeroEvent extends Event {
    
    private final HeroEventType type;
    
    protected HeroEvent(String name, HeroEventType type) {
        super(name);
        this.type = type;
    }

    public HeroEventType getHeroEventType() {
        return this.type;
    }
    
    public enum HeroEventType {
        HERO_CLASS_CHANGE,
        HERO_EXPERIENCE_CHANGE,
        HERO_LEVEL_CHANGE,
        HERO_JOIN_PARTY,
        HERO_LEAVE_PARTY,
        HERO_REGAIN_HEALTH,
        HERO_REGAIN_MANA,
        SKILL_DAMAGE,
        SKILL_USE,
        WEAPON_DAMAGE;
    }
}