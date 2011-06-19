package com.herocraftonline.dev.heroes.health;

import com.herocraftonline.dev.heroes.Heroes;

public class HealthReplacement {
    Heroes plugin;
    boolean enabled;
    
    public HealthReplacement(Heroes plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
    }
    
}
