package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillPulse extends ActiveSkill {

    public SkillPulse(Heroes plugin) {
        super(plugin, "Pulse");
        setDescription("Damages everyone around you");
        setUsage("/skill pulse");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill pulse"});
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 1);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        List<Entity> entities = hero.getPlayer().getNearbyEntities(5, 5, 5);
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity target = (LivingEntity) entity;
            if (target.equals(player)) {
                continue;
            }
            int damage = getSetting(hero.getHeroClass(), "damage", 1);
            EntityDamageEvent damageEvent = new EntityDamageEvent(player, DamageCause.CUSTOM, 0);
            Bukkit.getServer().getPluginManager().callEvent(damageEvent);
            if (damageEvent.isCancelled())
                return false;

            // See problem in SkillFireball.
            getPlugin().getDamageManager().addSpellTarget((Entity) target);
            target.damage(damage, player);
        }
        broadcastExecuteText(hero);
        return true;
    }

}
