package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillUseInfo;

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

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public Entity getEntity() {
        return entity;
    }

    public Hero getDamager() {
        return damager;
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

}
