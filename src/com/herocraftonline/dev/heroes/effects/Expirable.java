package com.herocraftonline.dev.heroes.effects;

public interface Expirable {

    public long getDuration();

    public long getExpiry();

    public boolean isExpired();
    
    public long getRemainingTime();
}
