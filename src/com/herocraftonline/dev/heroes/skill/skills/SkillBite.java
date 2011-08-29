package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

public class SkillBite extends TargettedSkill {

    public SkillBite(Heroes plugin) {
        super(plugin, "Bite");
        setDescription("Deals physical damage to the target");
        setUsage("/skill bite <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill bite" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 10);
        node.setProperty(Setting.MAX_DISTANCE.node(), 2);
        return node;
    }

    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target == player) {
            Messaging.send(player, "Invalid Target");
            return false;
        }

        EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
        getPlugin().getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled()) {
            Messaging.send(player, "Invalid target!");
            return false;
        }

        int damage = getSetting(hero.getHeroClass(), Setting.DAMAGE.node(), 10);
        getPlugin().getDamageManager().addSpellTarget(target, hero, this);
        target.damage(damage, player);
        broadcastExecuteText(hero, target);
        return true;
    }
}