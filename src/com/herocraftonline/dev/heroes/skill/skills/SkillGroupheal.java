package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillGroupheal extends ActiveSkill {

    public SkillGroupheal(Heroes plugin) {
        super(plugin);
        setName("Groupheal");
        setDescription("Heals all players around you");
        setUsage("/skill groupheal");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill groupheal");
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
        broadcastExecuteText(hero);
        return true;
    }

}
