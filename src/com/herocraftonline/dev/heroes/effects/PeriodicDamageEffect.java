package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicDamageEffect extends PeriodicExpirableEffect {

    protected int tickDamage;
    protected final Player applier;
    protected final Hero applyHero;

    public PeriodicDamageEffect(Skill skill, String name, long period, long duration, int tickDamage, Player applier) {
        super(skill, name, period, duration);
        this.tickDamage = tickDamage;
        this.applier = applier;
        this.applyHero = plugin.getHeroManager().getHero(applier);
        this.types.add(EffectType.HARMFUL);
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        Player player = hero.getPlayer();
        
        //Check if the target is damagable
        if (!skill.damageCheck(applier, player))
            return;
        
        skill.addSpellTarget(player, applyHero);
        player.damage(tickDamage, applier);
    }

    @Override
    public void tick(Creature creature) {
        super.tick(creature);
        skill.addSpellTarget(creature, applyHero);
        creature.damage(tickDamage, applier);
    }

    public void setTickDamage(int tickDamage) {
        this.tickDamage = tickDamage;
    }

    public int getTickDamage() {
        return tickDamage;
    }

    public Player getApplier() {
        return applier;
    }
    
    public Hero getApplierHero() {
        return applyHero;
    }

}
