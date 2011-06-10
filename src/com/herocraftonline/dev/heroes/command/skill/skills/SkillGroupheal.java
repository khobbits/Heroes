package com.herocraftonline.dev.heroes.command.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class SkillGroupheal extends ActiveSkill {

    public SkillGroupheal(Heroes plugin) {
        super(plugin);
        name = "Groupheal";
        description = "Heals all players around you";
        usage = "/skill groupheal";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill groupheal");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("heal-amount", 2);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        List<Entity> entities = hero.getPlayer().getNearbyEntities(5, 5, 5);
        for (Entity n : entities) {
            if (n instanceof Player) {
                Player pN = (Player) n;
                int healamount = getSetting(hero.getHeroClass(), "heal-amount", 2);
                pN.setHealth(pN.getHealth() + healamount);
            }
        }
        notifyNearbyPlayers(hero.getPlayer().getLocation(), useText, hero.getPlayer().getName(), name);
        return true;
    }

}
