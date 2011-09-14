package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class CombustEffect extends PeriodicExpirableEffect {
    
    private final Player applier;

    public CombustEffect(Skill skill, Player applier) {
        super(skill, "Combust", 50, 0);
        types.add(EffectType.FIRE);
        this.setPersistent(true);
        this.applier = applier;
    }
    
    @Override
    public void apply(Hero hero) {
        super.apply(hero);
    }
    
    @Override 
    public void remove(Hero hero) {
        super.remove(hero);
    }
    
    @Override 
    public void apply(Creature creature) {
        super.apply(creature);
    }
    
    @Override
    public void remove(Creature creature) {
        super.remove(creature);
    }
    
    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        if (hero.getPlayer().getFireTicks() == 0) {
            hero.removeEffect(this);
        }
    }
    
    @Override
    public void tick(Creature creature) {
        super.tick(creature);
        if (creature.getFireTicks() == 0) {
            plugin.getHeroManager().removeCreatureEffect(creature, this);
        }
    }

    public Player getApplier() {
        return applier;
    }
}
