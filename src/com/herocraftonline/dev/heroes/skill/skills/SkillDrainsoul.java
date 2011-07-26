package com.herocraftonline.dev.heroes.skill.skills;

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
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillDrainsoul extends TargettedSkill {

    public SkillDrainsoul(Heroes plugin) {
        super(plugin, "Drainsoul");
        setDescription("Absorb health from target");
        setUsage("/skill drainsoul <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill drainsoul" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("absorb-amount", 4);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (target.equals(hero.getPlayer())) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        // Throw a dummy damage event to make it obey PvP restricting plugins
        EntityDamageEvent event = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
        getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        int absorbAmount = getSetting(hero.getHeroClass(), "absorb-amount", 4);

        hero.setHealth(hero.getHealth() + (double) absorbAmount);
        hero.syncHealth();
        getPlugin().getDamageManager().addSpellTarget((Entity) target);
        target.damage(absorbAmount, player);

        broadcastExecuteText(hero, target);
        return true;
    }

}
