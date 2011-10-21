package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class WaterBreatheEffect extends ExpirableEffect {
    
    private final String applyText;
    private final String expireText;
    
    public WaterBreatheEffect(Skill skill, long duration, String applyText, String expireText) {
        super(skill, "WaterBreathing", duration);
        this.applyText = applyText;
        this.expireText = expireText;
        addMobEffect(13, (int) (duration / 1000) * 20, 0, false);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.BENEFICIAL);
        this.types.add(EffectType.WATER_BREATHING);
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
