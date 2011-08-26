package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillLickWounds extends ActiveSkill {

    public SkillLickWounds(Heroes plugin) {
        super(plugin, "LickWounds");
        setDescription("Heals your nearby wolves");
        setUsage("/skill lickwounds");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill lickwounds" });
    }
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.RADIUS.node(), 10);
        node.setProperty("heal-amount", .25); //% heal of maximum health
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int rangeSquared = (int) Math.pow(getSetting(hero.getHeroClass(), Setting.RADIUS.node(), 10), 2);
        if (getPlugin().getSkillMap().get("Wolf") == null) {
            return false;
        }
        
        SkillWolf skill = (SkillWolf) getPlugin().getSkillMap().get("Wolf");
        if (!hero.hasSkill("Wolf")) {
            Messaging.send(player, "You don't have the proper skills to do that!");
            return false;
        }
        double healthPerLevel = skill.getSetting(hero.getHeroClass(), "health-per-level", .25);
        int healthMax = skill.getSetting(hero.getHeroClass(), Setting.HEALTH.node(), 30) + (int) (healthPerLevel * hero.getLevel());
        double healed = healthMax * getSetting(hero.getHeroClass(), "heal-amount", .25);
        boolean used = false;
        for (Creature creature : hero.getSummons()) {
            if (!(creature instanceof Wolf) || creature.getLocation().distanceSquared(player.getLocation()) > rangeSquared ) 
                continue;
            
            if (creature.getHealth() + healed > healthMax) {
                creature.setHealth(healthMax);
            } else {
                creature.setHealth((int) (creature.getHealth() + healed));
            }
            used = true;
        }
        
        if (!used) {
            Messaging.send(player, "There are no nearby wolves to heal!");
            return false;
        }
        
        broadcastExecuteText(hero);
        return true;
    }

}
