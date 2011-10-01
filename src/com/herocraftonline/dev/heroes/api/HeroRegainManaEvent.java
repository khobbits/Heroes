package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.Cancellable;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

@SuppressWarnings("serial")
public class HeroRegainManaEvent extends HeroEvent implements Cancellable {

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
}
