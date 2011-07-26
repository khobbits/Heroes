package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillHarmtouch extends TargettedSkill {

    public SkillHarmtouch(Heroes plugin) {
        super(plugin, "Harmtouch");
        setDescription("Deals direct damage to the target");
        setUsage("/skill harmtouch [target]");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill harmtouch" });
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
        getPlugin().getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled())
            return false;
        getPlugin().getDamageManager().addSpellTarget((Entity) target);
        target.damage(damage, player);
        broadcastExecuteText(hero, target);
        return true;
    }

}
