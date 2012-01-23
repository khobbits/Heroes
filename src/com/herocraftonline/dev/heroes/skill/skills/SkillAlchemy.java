package com.herocraftonline.dev.heroes.skill.skills;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillAlchemy extends PassiveSkill {

    public SkillAlchemy(Heroes plugin) {
        super(plugin, "Alchemy");
        setDescription("You are able to craft potions!");
        setArgumentRange(0, 0);
        setTypes(SkillType.KNOWLEDGE, SkillType.ITEM);
        setEffectTypes(EffectType.BENEFICIAL);

        if (Heroes.useSpout()) {
            Bukkit.getServer().getPluginManager().registerEvents(new SkillInvListener(), plugin);
        }else {
            Heroes.log(Level.WARNING, "SkillAlchemy requires Spout! Remove from your skills directory if you will not use!");
        }
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set(Setting.LEVEL.node(), 1);
        return section;
    }

    public class SkillInvListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock().getType() != Material.BREWING_STAND) {
                return;
            }
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect(getName())) {
                Messaging.send(event.getPlayer(), "You don't have the required skills to brew potions!");
                event.setCancelled(true);
            }
        }
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
