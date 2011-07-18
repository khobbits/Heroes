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

public class SkillPulse extends ActiveSkill {

    public SkillPulse(Heroes plugin) {
        super(plugin);
        setName("Pulse");
        setDescription("Damages everyone around you");
        setUsage("/skill pulse");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill pulse");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 1);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        List<Entity> entities = hero.getPlayer().getNearbyEntities(5, 5, 5);
        for (Entity n : entities) {

            Player pN = (Player) n;
            int damage = getSetting(hero.getHeroClass(), "damage", 1);
            EntityDamageEvent damageEvent = new EntityDamageEvent(hero.getPlayer(), DamageCause.CUSTOM, damage);
            Bukkit.getServer().getPluginManager().callEvent(damageEvent);
            if (damageEvent.isCancelled()) return false;
            pN.damage(damage);
        }
        broadcastExecuteText(hero);
        return true;
    }

}
