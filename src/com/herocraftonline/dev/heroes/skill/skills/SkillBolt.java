package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillBolt extends TargettedSkill {

    public SkillBolt(Heroes plugin) {
        super(plugin, "Bolt");
        setDescription("Calls a bolt of lightning down on the target");
        setUsage("/skill bolt [target]");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill bolt" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 4);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (target.equals(player)) {
            Messaging.send(player, "Invalid target!");
            return false;
        }

        // PvP test
        EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
        getPlugin().getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled()) {
            Messaging.send(player, "Invalid target!");
            return false;
        }

        getPlugin().getDamageManager().addSpellTarget(target, hero, this);
        target.getWorld().strikeLightningEffect(target.getLocation());
        target.damage(getSetting(hero.getHeroClass(), "damage", 4), player);

        broadcastExecuteText(hero, target);
        return true;
    }
}
