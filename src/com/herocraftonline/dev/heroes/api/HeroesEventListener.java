package com.herocraftonline.dev.heroes.api;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public class HeroesEventListener extends CustomEventListener {

    /**
     * Called when a Hero gain experience
     * 
     * @param event
     */
    public void onExperienceGain(ExperienceGainEvent event) {

    }

    /**
     * Called when an entity is dealing damage with a weapon
     * 
     * @param event
     */
    public void onWeaponDamage(WeaponDamageEvent event) {

    }

    /**
     * Called when a Hero changes their class
     * 
     * @param event
     */
    public void onClassChange(ClassChangeEvent event) {

    }

    /**
     * Called when a Hero levels up
     * 
     * @param event
     */
    public void onHeroLevel(HeroLevelEvent event) {

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
    
    @Override
    public void onCustomEvent(Event event) {

        if (event instanceof ClassChangeEvent) {
            onClassChange((ClassChangeEvent) event);
        } else if (event instanceof ExperienceGainEvent) {
            onExperienceGain((ExperienceGainEvent) event);
        } else if (event instanceof WeaponDamageEvent) {
            onWeaponDamage((WeaponDamageEvent) event);
        } else if (event instanceof HeroLevelEvent) {
            onHeroLevel((HeroLevelEvent) event);
        } else if (event instanceof SkillDamageEvent) {
            onSkillDamage((SkillDamageEvent) event);
        } else if (event instanceof SkillUseEvent) {
            onSkillUse((SkillUseEvent) event);
        } else if (event instanceof HeroJoinPartyEvent) {
            onHeroJoinParty((HeroJoinPartyEvent) event);
        } else if (event instanceof HeroLeavePartyEvent) {
            onHeroLeaveParty((HeroLeavePartyEvent) event);
        } else if (event instanceof HeroRegainHealthEvent) {
            onHeroRegainHealth((HeroRegainHealthEvent) event);
        } else if (event instanceof HeroRegainManaEvent) {
            onHeroRegainMana((HeroRegainManaEvent) event);
        }
    }
}
