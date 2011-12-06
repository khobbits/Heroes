package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class CombustEffect extends PeriodicExpirableEffect {

    private final Player applier;
    private boolean expired = false;
    
    public CombustEffect(Skill skill, Player applier) {
        super(skill, "Combust", 50, 0);
        types.add(EffectType.FIRE);
        this.setPersistent(true);
        this.applier = applier;
    }

    @Override
    public void apply(LivingEntity lEntity) {
        super.apply(lEntity);
    }
    
    @Override
    public void apply(Hero hero) {
        super.apply(hero);
    }

    public Player getApplier() {
        return applier;
    }
    
    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void remove(LivingEntity lEntity) {
        super.remove(lEntity);
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
    }
    
    @Override
    public void tick(LivingEntity lEntity) {
        super.tick(lEntity);
        if (lEntity.getFireTicks() == 0) {
            this.expired = true;
        }
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        if (hero.getPlayer().getFireTicks() == 0) {
            this.expired = true;
        }
    }
}
