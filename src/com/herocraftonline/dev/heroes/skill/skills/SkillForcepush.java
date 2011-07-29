package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillForcepush extends TargettedSkill {

    public SkillForcepush(Heroes plugin) {
        super(plugin, "Forcepush");
        setDescription("Forces your target backwards");
        setArgumentRange(0, 1);
        setIdentifiers(new String[]{"skill forcepush"});
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target instanceof Player) {
            if ((Player) target == player) {
                return false;
            }
        }
        float pitch = player.getEyeLocation().getPitch();

        float multiplier = (90f + pitch) / 40f;
        Vector v = target.getVelocity().setY(1).add(target.getLocation().getDirection().setY(0).normalize().multiply(multiplier * -1));
        target.setVelocity(v);

        broadcastExecuteText(hero, target);
        return true;
    }

}
