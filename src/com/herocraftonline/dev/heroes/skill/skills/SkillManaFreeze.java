package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillManaFreeze extends TargettedSkill {

    public SkillManaFreeze(Heroes plugin) {
        super(plugin);
        setName("ManaFreeze");
        setDescription("Stops your target regening mana");
        setUsage("/skill manafreeze");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill manafreeze");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("duration", 5000);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        String playerName = player.getName();
        if (target instanceof Player) {
            Hero newHero = plugin.getHeroManager().getHero((Player) target);
            newHero.getEffects().putEffect(getName(), getSetting(hero.getHeroClass(), "duration", 5000L));
            notifyNearbyPlayers(player.getLocation(), getUseText(), playerName, getName());
            return true;
        } else {
            return false;
        }

    }
}
