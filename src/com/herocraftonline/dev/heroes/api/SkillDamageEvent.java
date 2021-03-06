package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

/**
 * This event is called when a skill would deal damage.  It is cancellable.
 */
@SuppressWarnings("serial")
public class SkillDamageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private int damage;
    private final Hero damager;
    private final Entity entity;
    private final Skill skill;
    private boolean cancelled = false;

    public SkillDamageEvent(int damage, Entity entity, SkillUseInfo skillInfo) {
        super("HeroesSkillDamageEvent");
        this.damage = damage;
        this.damager = skillInfo.getHero();
        this.skill = skillInfo.getSkill();
        this.entity = entity;
    }

    /**
     * @return the damage the skill will cause
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the hero that is causing the damage
     */
    public Hero getDamager() {
        return damager;
    }

    /**
     * @return the entity that is being dealt the damage
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @return the skill being used
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

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
