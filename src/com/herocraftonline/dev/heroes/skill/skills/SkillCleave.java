package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillCleave extends TargettedSkill {

    public SkillCleave(Heroes plugin) {
        super(plugin, "Cleave");
        setDescription("Cleaves your target and nearby enemies.");
        setUsage("/skill cleave <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill cleave");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("weapons", Util.axes);
        node.setProperty(Setting.MAX_DISTANCE.node(), 2);
        node.setProperty(Setting.RADIUS.node(), 3);
        node.setProperty("damage-multiplier", 1);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        
        Material item = player.getItemInHand().getType();
        if (!getSetting(hero, "weapons", Util.axes).contains(item.name())) {
            Messaging.send(player, "You can't cleave with that weapon!");
            return false;
        }

        HeroClass heroClass = hero.getHeroClass();
        int damage = heroClass.getItemDamage(item) == null ? 0 : heroClass.getItemDamage(item);
        damage *= getSetting(hero, "damage-multiplier", 1, false);
        target.damage(damage, player);
        int radius = getSetting(hero, Setting.RADIUS.node(), 3, false);
        for (Entity entity : target.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof LivingEntity) || !damageCheck(player, (LivingEntity) entity)) {
                continue;
            }

            ((LivingEntity) entity).damage(damage, player);
        }

        broadcastExecuteText(hero, target);
        return true;
    }
}
