package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SneakEffect extends PeriodicExpirableEffect {

    public SneakEffect(Skill skill, long period, long duration) {
        super(skill, "Sneak", period, duration);
        this.types.add(EffectType.BENEFICIAL);
        this.types.add(EffectType.PHYSICAL);
        this.types.add(EffectType.SNEAK);
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        Player player = hero.getPlayer();
        player.setSneaking(true);
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        Player player = hero.getPlayer();
        player.setSneaking(false);
        Messaging.send(player, "You are no longer sneaking!");
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);
        hero.getPlayer().setSneaking(false);
        hero.getPlayer().setSneaking(true);
    }
}