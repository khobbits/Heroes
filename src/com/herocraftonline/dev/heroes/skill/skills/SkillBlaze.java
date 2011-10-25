package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBlaze extends ActiveSkill {

    public SkillBlaze(Heroes plugin) {
        super(plugin, "Blaze");
        setDescription("Sets everyone around you on fire");
        setUsage("/skill blaze");
        setArgumentRange(0, 0);
        setIdentifiers("skill blaze");
        setTypes(SkillType.FIRE, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("fire-length", 3000);
        node.setProperty(Setting.RADIUS.node(), 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int range = getSetting(hero, Setting.RADIUS.node(), 5, false);
        List<Entity> entities = hero.getPlayer().getNearbyEntities(range, range, range);
        int fireTicks = getSetting(hero, "fire-length", 3000, false);
        boolean damaged = false;
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity lEntity = (LivingEntity) entity;
            
            if (!damageCheck(player, lEntity))
                continue;
            
            damaged = true;
            lEntity.setFireTicks(fireTicks);
        }
        
        if (!damaged) {
            Messaging.send(player, "No targets in range!");
            return false;
        }
        
        broadcastExecuteText(hero);
        return true;
    }
}
