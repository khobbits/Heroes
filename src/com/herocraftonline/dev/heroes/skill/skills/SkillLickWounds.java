package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillLickWounds extends ActiveSkill {

    public SkillLickWounds(Heroes plugin) {
        super(plugin, "LickWounds");
        setDescription("Heals your nearby wolves");
        setUsage("/skill lickwounds");
        setArgumentRange(0, 0);
        setIdentifiers("skill lickwounds", "skill lwounds");
        setTypes(SkillType.HEAL, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.RADIUS.node(), 10);
        node.setProperty("heal-amount", .25); // % heal of maximum health
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int rangeSquared = (int) Math.pow(getSetting(hero, Setting.RADIUS.node(), 10, false), 2);
        Skill skill = plugin.getSkillManager().getSkill("Wolf");
        if (skill == null)
            return false;

        if (!hero.hasSkill(skill) || skill.getSetting(hero, Setting.LEVEL.node(), 1, true) > hero.getLevel()) {
            Messaging.send(player, "You don't have the proper skills to do that!");
            return false;
        }
        double healthPerLevel = skill.getSetting(hero, "health-per-level", .25, false);
        int healthMax = skill.getSetting(hero, Setting.HEALTH.node(), 30, false) + (int) (healthPerLevel * hero.getLevel());
        double healed = healthMax * getSetting(hero, "heal-amount", .25, false);
        boolean used = false;
        for (Creature creature : hero.getSummons()) {
            if (!(creature instanceof Wolf) || creature.getLocation().distanceSquared(player.getLocation()) > rangeSquared) {
                continue;
            }

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
