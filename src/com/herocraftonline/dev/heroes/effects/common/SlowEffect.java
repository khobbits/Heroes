package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SlowEffect extends ExpirableEffect {

    private final String applyText = "$1 has been slowed!";
    private final String expireText = "$1 returned to normal speed!";
    
    public SlowEffect(Skill skill, long duration, int amplifier, boolean swing) {
        super(skill, "Slow", duration);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.HARMFUL);
        this.types.add(EffectType.SLOW);
        addMobEffect(2, (int) (duration / 1000) * 20, amplifier, false);
        if (swing) {
            addMobEffect(4, (int) (duration / 1000) * 20, amplifier, false);
        }
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
    
    @Override
    public void apply(Creature creature) {
        super.apply(creature);
        broadcast(creature.getLocation(), applyText, Messaging.getCreatureName(creature));
    }
    
    @Override
    public void remove(Creature creature) {
        super.remove(creature);
        broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature));
    }

}
