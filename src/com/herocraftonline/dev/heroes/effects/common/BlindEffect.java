package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class BlindEffect extends ExpirableEffect {

    private final String applyText;
    private final String expireText;
    
    public BlindEffect(Skill skill, long duration, String applyText, String expireText) {
        super(skill, "Blind", duration);
        this.applyText = applyText;
        this.expireText = expireText;
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.HARMFUL);
        this.types.add(EffectType.DISABLE);
        this.addMobEffect(15, (int) ((duration / 1000) * 20), 1, false);
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
