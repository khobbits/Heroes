package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

/**
 * Called when a hero uses a skill. This event is cancellable.
 */
@SuppressWarnings("serial")
public class SkillUseEvent extends HeroEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Skill skill;
    private final Hero hero;
    private int manaCost = 0;
    private int healthCost = 0;
    private int staminaCost = 0;
    private String[] args;
    private ItemStack reagentCost;
    private boolean cancelled = false;

    public SkillUseEvent(Skill skill, Player player, Hero hero, int manaCost, int healthCost, int staminaCost, ItemStack reagentCost, String[] args) {
        super("SkillUseEvent", HeroEventType.SKILL_USE);
        this.player = player;
        this.skill = skill;
        this.hero = hero;
        this.args = args;
        this.manaCost = manaCost;
        this.healthCost = healthCost;
        this.reagentCost = reagentCost;
        this.staminaCost = staminaCost;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * @return the healthCost
     */
    public int getHealthCost() {
        return healthCost;
    }

    /**
     * 
     * @return the staminaCost
     */
    public int getStaminaCost() {
        return staminaCost;
    }
    
    /**
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * @return the manaCost
     */
    public int getManaCost() {
        return manaCost;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    public ItemStack getReagentCost() {
        return reagentCost;
    }

    /**
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
     * @param args
     *            the args to set
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public void setCancelled(boolean val) {
        cancelled = val;
    }

    /**
     * @param healthCost
     *            the healthCost to set
     */
    public void setHealthCost(int healthCost) {
        this.healthCost = healthCost;
    }

    /**
     * 
     * @param staminaCost
     *          the staminaCost to set
     */
    public void setStaminaCost(int staminaCost) {
        this.staminaCost = staminaCost;
    }
    
    /**
     * @param manaCost
     *            the manaCost to set
     */
    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    /**
     * @param reagentCost
     */
    public void setReagentCost(ItemStack reagentCost) {
        this.reagentCost = reagentCost;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
