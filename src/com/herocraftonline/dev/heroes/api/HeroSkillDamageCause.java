package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.dev.heroes.skill.Skill;

/**
 * Represents a hero taking damage from a skill
 * @author sleak
 */
public class HeroSkillDamageCause extends HeroDamageCause {

    private final Entity attacker;
    private final Skill skill;

    public HeroSkillDamageCause(int damage, DamageCause cause, Entity attacker, Skill skill) {
        super(damage, cause);
        this.attacker = attacker;
        this.skill = skill;
    }

    /**
     * @return the attacker
     */
    public Entity getAttacker() {
        return attacker;
    }

    /**
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

}
