package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class QuickenEffect extends ExpirableEffect {

    private final String applyText;
    private final String expireText;

    public QuickenEffect(Skill skill, String name, long duration, int amplifier, String applyText, String expireText) {
        super(skill, name, duration);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.BENEFICIAL);
        addMobEffect(1, (int) (duration / 1000) * 20, amplifier, false);
        this.applyText = applyText;
        this.expireText = expireText;
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), applyText, player.getDisplayName());
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), expireText, player.getDisplayName());
    }
}
