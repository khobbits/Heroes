package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SlowEffect extends ExpirableEffect {

    private final String applyText;
    private final String expireText;
    private final Hero applier;
    
    public SlowEffect(Skill skill, String name, long duration, int amplifier, boolean swing, String applyText, String expireText, Hero applier) {
        super(skill, name, duration);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.HARMFUL);
        this.types.add(EffectType.SLOW);
        this.applyText = applyText;
        this.expireText = expireText;
        this.applier = applier;
        int tickDuration = (int) (duration / 1000) * 20;
        addMobEffect(2, tickDuration, amplifier, false);
        addMobEffect(8, tickDuration, -amplifier, false);
        if (swing) {
            addMobEffect(4, tickDuration, amplifier, false);
        }
    }
    public SlowEffect(Skill skill, long duration, int amplifier, boolean swing, String applyText, String expireText, Hero applier) {
        this(skill, "Slow", duration, amplifier, swing, expireText, expireText, applier);
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), applyText, player.getDisplayName(), applier.getPlayer().getDisplayName());
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), expireText, player.getDisplayName());
    }

    @Override
    public void apply(LivingEntity lEntity) {
        super.apply(lEntity);
        broadcast(lEntity.getLocation(), applyText, Messaging.getLivingEntityName(lEntity), applier.getPlayer().getDisplayName());
    }

    @Override
    public void remove(LivingEntity lEntity) {
        super.remove(lEntity);
        broadcast(lEntity.getLocation(), expireText, Messaging.getLivingEntityName(lEntity));
    }
}
