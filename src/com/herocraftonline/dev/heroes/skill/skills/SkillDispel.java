package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillDispel extends TargettedSkill {

    public SkillDispel(Heroes plugin) {
        super(plugin);
        setName("Dispel");
        setDescription("Removes all effects from your target");
        setUsage("/skill dispel");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill dispel");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            return false;
        }

        Player targetPlayer = (Player) target;
        Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
        for (String s : targetHero.getEffects()) {
            if (targetHero.getEffectExpiry(s) > 0) {
                targetHero.removeEffect(s);
            }
        }

        notifyNearbyPlayers(player.getLocation(), getUseText(), player.getName(), getName(), getEntityName(target));
        return true;
    }

}
