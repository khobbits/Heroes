package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillLayhands extends TargettedSkill {

    public SkillLayhands(Heroes plugin) {
        super(plugin);
        setName("Layhands");
        setDescription("Heals the target to full");
        setUsage("/skill layhands [target]");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill layhands");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        target.setHealth(20);
        notifyNearbyPlayers(player.getLocation(), getUseText(), player.getName(), getName(), target == player ? "himself" : getEntityName(target));
        return true;
    }
}
