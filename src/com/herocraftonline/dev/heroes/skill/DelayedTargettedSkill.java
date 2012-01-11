package com.herocraftonline.dev.heroes.skill;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DelayedTargettedSkill extends DelayedSkill {

    private final LivingEntity target;
    
    public DelayedTargettedSkill(String identifier, Player player, long warmup, Skill skill, LivingEntity target, String[] args) {
        super(identifier, player, warmup, skill, args);
        this.target = target;
    }

    public LivingEntity getTarget() {
        return target;
    }

}
