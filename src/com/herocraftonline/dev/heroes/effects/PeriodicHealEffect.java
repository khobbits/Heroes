package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Properties;

public class PeriodicHealEffect extends PeriodicEffect {

    private int tickHealth;
    private final Player applier;
    
    public PeriodicHealEffect(Skill skill, String name, long period, long duration, int tickHealth, Player applier) {
        super(skill, name, period, duration);
        this.tickHealth = tickHealth;
        this.applier = applier;
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, tickHealth, this.getSkill());
        getSkill().getPlugin().getServer().getPluginManager().callEvent(hrhEvent);
        if (hrhEvent.isCancelled()) return;

        hero.setHealth(hero.getHealth() + hrhEvent.getAmount());
        hero.syncHealth();
    }
    
    @Override
    public void tick(Creature creature) {
        super.tick(creature);
        
        CreatureType cType = Properties.getCreatureFromEntity(creature);
        if (cType == null) return;
        int maxHealth = getSkill().getPlugin().getDamageManager().getCreatureHealth(cType);
        creature.setHealth(tickHealth + creature.getHealth());
        if (creature.getHealth() > maxHealth) {
            creature.setHealth(maxHealth);
        } 
    }

    public void setTickHealth(int tickHealth) {
        this.tickHealth = tickHealth;
    }

    public int getTickDamage() {
        return tickHealth;
    }

    public Player getApplier() {
        return applier;
    }

}
