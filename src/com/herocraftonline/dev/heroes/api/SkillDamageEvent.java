package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

@SuppressWarnings("serial")
public class SkillDamageEvent extends Event implements Cancellable {

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

    public int getDamage() {
        return damage;
    }

    public Hero getDamager() {
        return damager;
    }

    public Entity getEntity() {
        return entity;
    }

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

}
