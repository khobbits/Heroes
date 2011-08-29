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
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSmite extends TargettedSkill {

    public SkillSmite(Heroes plugin) {
        super(plugin, "Smite");
        setDescription("Uses smite on a player");
        setUsage("/skill smite");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill smite" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 10);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target == player) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        //PvP Check
        EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
        getPlugin().getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled()) {
            return false;
        }
        
        int damage = getSetting(hero.getHeroClass(), Setting.DAMAGE.node(), 10);
        getPlugin().getDamageManager().addSpellTarget((Entity) target, hero, this);
        target.damage(damage, player);
        broadcastExecuteText(hero, target);
        return true;
    }

}
