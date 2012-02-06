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
    private int lastFireTickCount = -1;
    
    public CombustEffect(Skill skill, Player applier) {
        super(skill, "Combust", 10, 0);
        types.add(EffectType.FIRE);
        this.setPersistent(true);
        this.applier = applier;
    }

    @Override
    public void apply(LivingEntity lEntity) {
        super.apply(lEntity);
        lastFireTickCount = lEntity.getFireTicks();
    }
    
    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        lastFireTickCount = hero.getPlayer().getFireTicks();
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
        
        int fireTicks = lEntity.getFireTicks();
        if (lastFireTickCount - fireTicks >= 10) {
            lEntity.setNoDamageTicks(0);
        }
        lastFireTickCount = fireTicks;
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        Player player = hero.getPlayer();
        if (player.getFireTicks() == 0) {
            this.expired = true;
        }

        int fireTicks = player.getFireTicks();
        if (lastFireTickCount - fireTicks >= 10) {
            player.setNoDamageTicks(0);
        }
        lastFireTickCount = fireTicks;
    }
}
