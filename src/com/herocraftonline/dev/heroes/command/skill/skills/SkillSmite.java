package com.herocraftonline.dev.heroes.command.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillSmite extends TargettedSkill {

    public SkillSmite(Heroes plugin) {
        super(plugin);
        name = "Smite";
        description = "Uses smite on a player";
        usage = "/skill smite";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill smite");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 10);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target == player) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        int damage = getSetting(hero.getHeroClass(), "damage", 10);
        EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, damage);
        plugin.getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled()) {
            return false;
        }
        target.damage(damage, player);
        String targetName = target instanceof Player ? ((Player) target).getName() : target.getClass().getSimpleName().substring(5);
        notifyNearbyPlayers(player.getLocation(), "$1 used $2 on $3!", player.getName(), name, target == player ? "himself" : targetName);
        return true;
    }

}
