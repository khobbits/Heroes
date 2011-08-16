package com.herocraftonline.dev.heroes.skill;

import com.herocraftonline.dev.heroes.persistence.Hero;

public class SkillUseInfo {

    private final Hero hero;
    private final Skill skill;
    
    public SkillUseInfo(Hero hero, Skill skill) {
        this.hero = hero;
        this.skill = skill;
    }

    /**
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }
}
