package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

/**
 * Called when a hero regains health, primarily from a skill.  This event is able to be cancelled.
 */
@SuppressWarnings("serial")
public class HeroRegainHealthEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private int amount;
    private final Hero hero;
    private final Skill skill;
    private boolean cancelled = false;

    public HeroRegainHealthEvent(Hero hero, int amount, Skill skill) {
        super("HeroRegainHealthEvent");
        this.amount = amount;
        this.hero = hero;
        this.skill = skill;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Supports null values in case a skill was not used to generate the event!
     * 
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @param amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
