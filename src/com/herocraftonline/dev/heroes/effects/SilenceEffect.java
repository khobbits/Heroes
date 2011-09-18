package com.herocraftonline.dev.heroes.effects;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SilenceEffect extends ExpirableEffect {

    private final String expireText = "$1 is no longer silenced!";

    public SilenceEffect(Skill skill, long duration) {
        super(skill, "Silence", duration);
        types.add(EffectType.DISPELLABLE);
        types.add(EffectType.HARMFUL);
        types.add(EffectType.SILENCE);
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        broadcast(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getDisplayName());
    }
}