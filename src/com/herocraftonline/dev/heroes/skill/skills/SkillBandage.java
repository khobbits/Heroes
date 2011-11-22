package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBandage extends TargettedSkill {

    public SkillBandage(Heroes plugin) {
        super(plugin, "Bandage");
        setDescription("Bandages the target");
        setUsage("/skill bandage <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill bandage");
        setTypes(SkillType.HEAL, SkillType.PHYSICAL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("health", 5);
        node.setProperty(Setting.MAX_DISTANCE.node(), 5);
        node.setProperty(Setting.REAGENT.node(), "PAPER");
        node.setProperty(Setting.REAGENT_COST.node(), 1);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            return SkillResult.INVALID_TARGET;
        }

        Hero targetHero = plugin.getHeroManager().getHero((Player) target);
        int hpPlus = getSetting(hero, "health", 5, false);
        double targetHealth = targetHero.getHealth();

        if (targetHealth >= targetHero.getMaxHealth()) {
            if (player.equals(targetHero.getPlayer())) {
                Messaging.send(player, "You are already at full health.");
            } else {
                Messaging.send(player, "Target is already fully healed.");
            }
            return SkillResult.INVALID_TARGET_NO_MSG;
        }

        HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(targetHero, hpPlus, this);
        plugin.getServer().getPluginManager().callEvent(hrhEvent);
        if (hrhEvent.isCancelled()) {
            Messaging.send(player, "Unable to heal the target at this time!");
            return SkillResult.CANCELLED;
        }

        targetHero.setHealth(targetHealth + hrhEvent.getAmount());
        targetHero.syncHealth();

        // Bandage cures Bleeding!
        for (Effect effect : targetHero.getEffects()) {
            if (effect.isType(EffectType.BLEED)) {
                targetHero.removeEffect(effect);
            }
        }

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }
}
