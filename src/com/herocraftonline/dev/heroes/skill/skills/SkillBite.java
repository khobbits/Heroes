package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

public class SkillBite extends TargettedSkill {

    public SkillBite(Heroes plugin) {
        super(plugin, "Bite");
        setDescription("Deals physical damage to the target");
        setUsage("/skill bite <target>");
        setArgumentRange(0, 1);
        
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);
        
        setIdentifiers(new String[] { "skill bite" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 10);
        node.setProperty(Setting.MAX_DISTANCE.node(), 2);
        return node;
    }

    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target.equals(player) || hero.getSummons().contains(target)) {
            Messaging.send(player, "Invalid Target");
            return false;
        }

        //Check if the target is damagable
        if (!damageCheck(player, target))
            return false;

        int damage = getSetting(hero.getHeroClass(), Setting.DAMAGE.node(), 10);
        addSpellTarget(target, hero);
        target.damage(damage, player);
        broadcastExecuteText(hero, target);
        return true;
    }
}