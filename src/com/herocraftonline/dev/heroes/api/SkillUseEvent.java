package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

@SuppressWarnings("serial")
public class SkillUseEvent extends Event implements Cancellable {

    private final Player player;
    private final Skill skill;
    private final Hero hero;
    private int manaCost = 0;
    private int healthCost = 0;
    private String[] args;
    private ItemStack reagentCost;
    private boolean cancelled = false;

    public SkillUseEvent(Skill skill, Player player, Hero hero, int manaCost, int healthCost, ItemStack reagentCost, String[] args) {
        super("SkillUseEvent");
        this.player = player;
        this.skill = skill;
        this.hero = hero;
        this.args = args;
        this.manaCost = manaCost;
        this.healthCost = healthCost;
        this.reagentCost = reagentCost;
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
     * @param manaCost
     *            the manaCost to set
     */
    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public void setReagentCost(ItemStack reagentCost) {
        this.reagentCost = reagentCost;
    }

}
