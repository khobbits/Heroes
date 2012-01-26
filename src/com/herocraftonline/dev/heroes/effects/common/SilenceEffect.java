package com.herocraftonline.dev.heroes.effects.common;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.DelayedSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SilenceEffect extends ExpirableEffect {

    private final String expireText = "$1 is no longer silenced!";

    public SilenceEffect(Skill skill, long duration) {
        super(skill, "Silence", duration);
        types.add(EffectType.DISPELLABLE);
        types.add(EffectType.HARMFUL);
        types.add(EffectType.SILENCE);
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        DelayedSkill dSkill = hero.getDelayedSkill();
        if (dSkill != null && dSkill.getSkill().isType(SkillType.SILENCABLE)) {
            hero.cancelDelayedSkill();
        }
    }
    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        broadcast(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getDisplayName());
    }
}