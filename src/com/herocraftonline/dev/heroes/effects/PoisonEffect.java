package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.skill.Skill;

public class PoisonEffect extends PeriodicDamageEffect {

    public PoisonEffect(Skill skill, String name, long period, long duration, int tickDamage, Player applier) {
        super(skill, name, period, duration, tickDamage, applier);
    }
}
