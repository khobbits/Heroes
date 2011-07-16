package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillBolt extends TargettedSkill {

    public SkillBolt(Heroes plugin) {
        super(plugin);
        setName("Bolt");
        setDescription("Calls a bolt of thunder down on the target");
        setUsage("/skill bolt [target]");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill bolt");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("range", 10);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (target.equals(player)) {
            return false;
        }

        EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
        plugin.getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled()) {
            return false;
        }

        int range = getSetting(hero.getHeroClass(), "range", 10);
        List<Entity> entityList = target.getNearbyEntities(range, range, range);
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity) {
                if (entity != player) {
                    // Throw a dummy damage event to make it obey PvP restricting plugins
                    EntityDamageEvent event = new EntityDamageByEntityEvent(player, target, DamageCause.ENTITY_ATTACK, 0);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        target.getWorld().strikeLightning(entity.getLocation());
                    }
                }
            }
        }
        target.getWorld().strikeLightning(target.getLocation());

        broadcastExecuteText(hero, target);
        return true;
    }
}
