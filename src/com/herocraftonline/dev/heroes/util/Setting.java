package com.herocraftonline.dev.heroes.util;

/**
 * Stores node identifiers for settings
 * 
 *
 */
public enum Setting {
    
    AMOUNT("amount"),
    APPLY_TEXT("apply-text"),
    COOLDOWN("cooldown"),
    DAMAGE("damage"),
    DURATION("duration"),
    EXP("exp"),
    EXPIRE_TEXT("expire-text"),
    LEVEL("level"),
    MANA("mana"),
    MAX_DISTANCE("max-distance"),
    PERIOD("period"),
    RADIUS("radius"),
    REAGENT("reagent"),
    REAGENT_COST("reagent-cost"),
    UNAPPLY_TEXT("unapply-text"),
    USE_TEXT("use-text");

    private final String node;
    
    Setting(String node) {
        this.node = node;
    }
    
    public String node() {
        return this.node;
    }
}
