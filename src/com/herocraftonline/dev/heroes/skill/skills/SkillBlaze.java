package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillBlaze extends ActiveSkill {

    public SkillBlaze(Heroes plugin) {
        super(plugin);
        name = "Blaze";
        description = "Sets everyone around you on fire";
        usage = "/skill blaze";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill blaze");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("fire-length", 3000);
        node.setProperty("range", 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        int range = getSetting(hero.getHeroClass(), "range", 5);
        List<Entity> entities = hero.getPlayer().getNearbyEntities(range, range, range);
        int fireTicks = getSetting(hero.getHeroClass(), "fire-length", 3000);
        for (Entity n : entities) {
            Player pN = (Player) n;
            EntityDamageEvent damageEvent = new EntityDamageEvent(hero.getPlayer(), DamageCause.ENTITY_ATTACK, 0);
            Bukkit.getServer().getPluginManager().callEvent(damageEvent);
            if (damageEvent.isCancelled()) {
                return false;
            }
            pN.setFireTicks(fireTicks);
        }
        notifyNearbyPlayers(hero.getPlayer().getLocation(), getUseText(), hero.getPlayer().getName(), name);
        return true;
    }

}
