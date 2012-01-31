package com.herocraftonline.dev.heroes.effects.common;

import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class ImbueEffect extends Effect {

    private String description;
    
    public ImbueEffect(Skill skill, String name) {
        super(skill, name);
        this.types.add(EffectType.IMBUE);
        this.types.add(EffectType.BENEFICIAL);
    }
    
    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        for (Effect effect : hero.getEffects()) {
            if (effect.equals(this)) {
                continue;
            }

            if (effect.isType(EffectType.IMBUE)) {
                hero.removeEffect(effect);
            }
        }
    }
    
    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        Messaging.send(hero.getPlayer(), "Your weapon is no longer imbued with $1", description);
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
}
