package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SafeFallEffect extends ExpirableEffect {
    
    private String applyText = "$1 has braced for landing!";
    private String expireText = "$1 has lost safefall!";
    
    public SafeFallEffect(Skill skill, long duration) {
        super(skill, "Safefall", duration);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.BENEFICIAL);
        this.types.add(EffectType.SAFEFALL);
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
