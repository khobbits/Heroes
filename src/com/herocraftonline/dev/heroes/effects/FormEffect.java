package com.herocraftonline.dev.heroes.effects;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class FormEffect extends Effect {

    public FormEffect(Skill skill, String name) {
        super(skill, name);
        this.types.add(EffectType.FORM);
        this.types.add(EffectType.BENEFICIAL);
    }
    
    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        for (Effect effect : hero.getEffects()) {
            if (effect.equals(this))
                continue;
            
            if (effect.isType(EffectType.FORM)) {
                hero.removeEffect(effect);
            }
        }
    }
}
