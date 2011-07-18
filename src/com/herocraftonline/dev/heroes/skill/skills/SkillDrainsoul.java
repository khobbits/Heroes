package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillDrainsoul extends TargettedSkill {

    public SkillDrainsoul(Heroes plugin) {
        super(plugin);
        setName("Drainsoul");
        setDescription("Absorb health from target");
        setUsage("/skill drainsoul <target>");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill drainsoul");
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

        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            if (targetPlayer.getName().equalsIgnoreCase(player.getName())) return false;
        }

        // Throw a dummy damage event to make it obey PvP restricting plugins
        EntityDamageEvent event = new EntityDamageByEntityEvent(player, target, DamageCause.ENTITY_ATTACK, 0);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        int absorbAmount = getSetting(hero.getHeroClass(), "absorb-amount", 4);

        if (hero.getPlayer().getHealth() + absorbAmount > 20) {
            absorbAmount = 20 - hero.getPlayer().getHealth();
        }

        player.setHealth(player.getHealth() + absorbAmount);
        target.damage(absorbAmount);

        broadcastExecuteText(hero, target);
        return true;
    }

}
