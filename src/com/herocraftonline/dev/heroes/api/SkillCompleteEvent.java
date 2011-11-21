package com.herocraftonline.dev.heroes.api;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

@SuppressWarnings("serial")
/**
 * Called when a hero completes a skill - either successfully or unsuccessfully.
 * @author sleak
 */
public class SkillCompleteEvent extends HeroEvent {

    private final Hero hero;
    private final Skill skill;
    private final SkillResult result;
    
    public SkillCompleteEvent(Hero hero, Skill skill, SkillResult result) {
        super("SkillCompleteEvent", HeroEventType.SKILL_COMPLETE);
        this.hero = hero;
        this.skill = skill;
        this.result = result;
    }

    /**
     * Returns the hero that used the skill
     * @return Hero that used the skill
     */
    public Hero getHero() {
        return hero;
    }
    
    /**
     * Returns the skill used
     * @return Skill used
     */
    public Skill getSkill() {
        return skill;
    }
    
    /**
     * returns the result of the skill completion
     * @return SkillResult of skill
     */
    public SkillResult getResult() {
        return result;
    }
}
