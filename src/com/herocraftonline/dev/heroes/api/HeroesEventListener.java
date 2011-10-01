package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public class HeroesEventListener extends CustomEventListener {

    /**
     * Called when a Hero changes their class
     * 
     * @param event
     */
    public void onClassChange(ClassChangeEvent event) {

    }

    @Override
    public void onCustomEvent(Event event) {
        if (!(event instanceof HeroEvent))
            return;
        
        switch (((HeroEvent) event).getHeroEventType()) {
        case HERO_CLASS_CHANGE :
            onClassChange((ClassChangeEvent) event);
            break;
        case HERO_EXPERIENCE_CHANGE :
            onExperienceChange((ExperienceChangeEvent) event);
            break;
        case WEAPON_DAMAGE :
            onWeaponDamage((WeaponDamageEvent) event);
            break;
        case HERO_LEVEL_CHANGE :
            onHeroChangeLevel((HeroChangeLevelEvent) event);
            break;
        case SKILL_DAMAGE :
            onSkillDamage((SkillDamageEvent) event);
            break;
        case SKILL_USE :
            onSkillUse((SkillUseEvent) event);
            break;
        case HERO_JOIN_PARTY :
            onHeroJoinParty((HeroJoinPartyEvent) event);
            break;
        case HERO_LEAVE_PARTY :
            onHeroLeaveParty((HeroLeavePartyEvent) event);
            break;
        case HERO_REGAIN_HEALTH :
            onHeroRegainHealth((HeroRegainHealthEvent) event);
            break;
        case HERO_REGAIN_MANA :
            onHeroRegainMana((HeroRegainManaEvent) event);
            break;
        }
    }

    /**
     * Called when a Hero gains or loses experience
     * 
     * @param event
     */
    public void onExperienceChange(ExperienceChangeEvent event) {

    }

    /**
     * Called when a Hero's level changes
     * 
     * @param event
     */
    public void onHeroChangeLevel(HeroChangeLevelEvent event) {

    }

    /**
     * Called when a hero is joining a group
     * 
     * @param event
     */
    public void onHeroJoinParty(HeroJoinPartyEvent event) {

    }

    /**
     * Called when a hero is leaving a party
     * 
     * @param event
     */
    public void onHeroLeaveParty(HeroLeavePartyEvent event) {

    }

    /**
     * Called when a hero regains health
     * 
     * @param event
     */
    public void onHeroRegainHealth(HeroRegainHealthEvent event) {

    }

    /**
     * Called when a hero regains mana
     * 
     * @param event
     */
    public void onHeroRegainMana(HeroRegainManaEvent event) {

    }

    /**
     * Called when a target takes damage as a result of a skill
     * 
     * @param event
     */
    public void onSkillDamage(SkillDamageEvent event) {

    }

    /**
     * Called when a skill is being used by a player
     * 
     * @param event
     */
    public void onSkillUse(SkillUseEvent event) {

    }

    /**
     * Called when an entity is dealing damage with a weapon
     * 
     * @param event
     */
    public void onWeaponDamage(WeaponDamageEvent event) {

    }
}
