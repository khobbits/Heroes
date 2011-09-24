package com.herocraftonline.dev.heroes.effects;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class ImbueEffect extends ExpirableEffect {

    private int applicationsLeft;
    String description;
    
    public ImbueEffect(Skill skill, String name, long duration, int numAttacks) {
        super(skill, name, duration);
        this.types.add(EffectType.IMBUE);
        this.types.add(EffectType.BENEFICIAL);
        this.applicationsLeft = numAttacks;
    }

    public int getApplicationsLeft() {
        return applicationsLeft;
    }
    
    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        for (Effect effect : hero.getEffects()) {
            if (effect.equals(this))
                continue;

            if (effect.isType(EffectType.IMBUE))
                hero.removeEffect(effect);
        }
    }
    
    @Override
    public void remove(Hero hero) {
        Messaging.send(hero.getPlayer(), "Your weapon is no longer imbued with $1", description);
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setApplicationsLeft(int applicationsLeft) {
        this.applicationsLeft = applicationsLeft;
    }
    
    public boolean hasNoApplications() {
        return this.applicationsLeft == 0;
    }
}
