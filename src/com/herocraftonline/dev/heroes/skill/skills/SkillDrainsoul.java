package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillDrainsoul extends TargettedSkill {

    public SkillDrainsoul(Heroes plugin) {
        super(plugin, "Drainsoul");
        setDescription("Absorb health from target");
        setUsage("/skill drainsoul <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill drainsoul" });
        
        setTypes(SkillType.DARK, SkillType.SILENCABLE, SkillType.DAMAGING);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("absorb-amount", 4);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (target.equals(player) || hero.getSummons().contains(target)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        //Check if the target is damagable
        if (!damageCheck(player, target))
            return false;

        int absorbAmount = getSetting(hero.getHeroClass(), "absorb-amount", 4);

        hero.setHealth(hero.getHealth() + (double) absorbAmount);
        hero.syncHealth();
        addSpellTarget(target, hero);
        target.damage(absorbAmount, player);

        broadcastExecuteText(hero, target);
        return true;
    }

}
