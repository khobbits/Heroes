package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

/**
 * This event is called when a hero would regain mana, this event is cancellable
 */
@SuppressWarnings("serial")
public class HeroRegainManaEvent extends HeroEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private int amount;
    private final Hero hero;
    private final Skill skill;
    private boolean cancelled = false;

    public HeroRegainManaEvent(Hero hero, int amount, Skill skill) {
        super("HeroRegainManaEvent", HeroEventType.HERO_REGAIN_MANA);
        this.hero = hero;
        this.amount = amount;
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
     * @param amount
     *            the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void setCancelled(boolean val) {
        this.cancelled = val;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
