package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Util;

public class PeriodicHealEffect extends PeriodicExpirableEffect {

    private int tickHealth;
    private final Player applier;

    public PeriodicHealEffect(Skill skill, String name, long period, long duration, int tickHealth, Player applier) {
        super(skill, name, period, duration);
        this.tickHealth = tickHealth;
        this.applier = applier;
        this.types.add(EffectType.BENEFICIAL);
        this.types.add(EffectType.HEAL);
    }

    public Player getApplier() {
        return applier;
    }

    public int getTickDamage() {
        return tickHealth;
    }

    public void setTickHealth(int tickHealth) {
        this.tickHealth = tickHealth;
    }

    @Override
    public void tick(LivingEntity lEntity) {
        super.tick(lEntity);

        CreatureType cType = Util.getCreatureFromEntity(lEntity);
        if (cType == null)
            return;
        int maxHealth = plugin.getDamageManager().getEntityHealth(cType);
        lEntity.setHealth(tickHealth + lEntity.getHealth());
        if (lEntity.getHealth() > maxHealth) {
            lEntity.setHealth(maxHealth);
        }
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, tickHealth, skill);
        plugin.getServer().getPluginManager().callEvent(hrhEvent);
        if (hrhEvent.isCancelled())
            return;

        hero.setHealth(hero.getHealth() + hrhEvent.getAmount());
        hero.syncHealth();
    }

}
