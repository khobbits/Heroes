package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

@SuppressWarnings("serial")
public class HeroRegainManaEvent extends Event implements Cancellable {

    private int amount;
    private final Hero hero;
    private final Skill skill;
    private boolean cancelled = false;
    
    public HeroRegainManaEvent(Hero hero, int amount, Skill skill) {
        super("HeroRegainManaEvent");
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
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
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

    @Override
    public void setCancelled(boolean val) {
        this.cancelled = val;
    }
}
