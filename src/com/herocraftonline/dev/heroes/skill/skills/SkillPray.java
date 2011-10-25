package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPray extends TargettedSkill {

    public SkillPray(Heroes plugin) {
        super(plugin, "Pray");
        setDescription("Heals the target");
        setUsage("/skill pray <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill pray");
        setTypes(SkillType.HEAL, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("health", 10);
        node.setProperty(Setting.MAX_DISTANCE.node(), 25);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            Messaging.send(player, "Invalid target!");
            return false;
        }

        Hero targetHero = plugin.getHeroManager().getHero((Player) target);
        int hpPlus = getSetting(hero, "health", 10, false);
        double targetHealth = targetHero.getHealth();

        if (targetHealth >= targetHero.getMaxHealth()) {
            if (player.equals(targetHero.getPlayer())) {
                Messaging.send(player, "You are already at full health.");

            } else {
                Messaging.send(player, "Target is already fully healed.");
            }
            return false;
        }

        HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(targetHero, hpPlus, this);
        plugin.getServer().getPluginManager().callEvent(hrhEvent);
        if (hrhEvent.isCancelled()) {
            Messaging.send(player, "Unable to heal the target at this time!");
            return false;
        }

        targetHero.setHealth(targetHealth + hrhEvent.getAmount());
        targetHero.syncHealth();
        broadcastExecuteText(hero, target);
        return true;
    }
}
