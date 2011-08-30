package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class PeriodicDamageEffect extends PeriodicEffect implements Harmful {

    private int tickDamage;
    private final Player applier;
    private final Hero applyHero;

    public PeriodicDamageEffect(Skill skill, String name, long period, long duration, int tickDamage, Player applier) {
        super(skill, name, period, duration);
        this.tickDamage = tickDamage;
        this.applier = applier;
        this.applyHero = plugin.getHeroManager().getHero(applier);
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        Player player = hero.getPlayer();
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

}
