package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class RootEffect extends ExpirableEffect {

    private final String applyText = "$1 was rooted!";
    private final String expireText = "Root faded from $1!";

    public RootEffect(Skill skill, long duration) {
        super(skill, "Root", duration);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.ROOT);
        this.types.add(EffectType.HARMFUL);
        int tickDuration = (int) duration / 1000 * 20;
        this.addMobEffect(2, tickDuration, 5, false);
        this.addMobEffect(8, tickDuration, -5, false);
    }

    @Override
    public void apply(LivingEntity lEntity) {
        super.apply(lEntity);
        broadcast(lEntity.getLocation(), applyText, Messaging.getLivingEntityName(lEntity));
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), applyText, player.getDisplayName());
    }

    @Override
    public void remove(LivingEntity lEntity) {
        super.remove(lEntity);
        broadcast(lEntity.getLocation(), expireText, Messaging.getLivingEntityName(lEntity));
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), expireText, player.getDisplayName());
    }
}