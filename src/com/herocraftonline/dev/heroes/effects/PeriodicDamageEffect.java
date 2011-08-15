package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicDamageEffect extends PeriodicEffect {

    private int tickDamage;
    private final Player applier;
    
    public PeriodicDamageEffect(Skill skill, String name, long period, long duration, int tickDamage, Player applier) {
        super(skill, name, period, duration);
        this.tickDamage = tickDamage;
        this.applier = applier;
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        Player player = hero.getPlayer();
        getSkill().getPlugin().getDamageManager().addSpellTarget(player);
        player.damage(tickDamage, applier);
    }
    
    @Override
    public void tick(Creature creature) {
        super.tick(creature);
        getSkill().getPlugin().getDamageManager().addSpellTarget(creature);
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

}
