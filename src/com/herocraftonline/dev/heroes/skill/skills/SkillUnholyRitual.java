package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillUnholyRitual extends TargettedSkill {

    public SkillUnholyRitual(Heroes plugin) {
        super(plugin, "UnholyRitual");
        setDescription("Target Zombie or Skeleton is sacrificed, necromancer receives mana");
        setUsage("/skill unholyritual");
        setArgumentRange(0, 0);
        setIdentifiers("skill unholyritual", "skill uritual");
        setTypes(SkillType.DARK, SkillType.SILENCABLE, SkillType.DAMAGING);
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (!(target instanceof Zombie) && !(target instanceof Skeleton)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        addSpellTarget(target, hero);
        target.damage(target.getHealth(), player);
        hero.setMana(hero.getMana() + 20);
        broadcastExecuteText(hero, target);
        return true;
    }

}
