package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.ContainerEnchantTable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryEnchantEvent;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillEnchant extends PassiveSkill {

    public SkillEnchant(Heroes plugin) {
        super(plugin, "Enchant");

        setDescription("You are able to enchant items.");
        setArgumentRange(0, 0);
        setTypes(SkillType.KNOWLEDGE, SkillType.ITEM);
        setEffectTypes(EffectType.BENEFICIAL);

        if (Heroes.useSpout()) {
            Bukkit.getServer().getPluginManager().registerEvents(new SkillSpoutListener(this), plugin);
        } else {
            Heroes.log(Level.WARNING, "SkillEnchant requires Spout! Remove from your skills directory if you will not use!");
        }
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set("PROTECTION_ENVIRONMENTAL", 200);
        section.set("PROTECTION_FIRE", 1);
        section.set("PROTECTION_FALL", 1);
        section.set("PROTECTION_EXPLOSIONS", 200);
        section.set("PROTECTION_PROJECTILE", 200);
        section.set("OXYGEN", 1);
        section.set("WATER_WORKER", 1);
        section.set("DAMAGE_ALL", 200);
        section.set("DAMAGE_UNDEAD", 200);
        section.set("DAMAGE_ARTHROPODS", 200);
        section.set("KNOCKBACK", 1);
        section.set("FIRE_ASPECT", 1);
        section.set("LOOT_BONUS_MOBS", 1);
        section.set("DIG_SPEED", 1);
        section.set("SILK_TOUCH", 200);
        section.set("DURABILITY", 1);
        section.set("LOOT_BONUS_BLOCKS", 1);
        section.set("ARROW_DAMAGE", 200);
        section.set("ARROW_KNOCKBACK", 1);
        section.set("ARROW_FIRE", 1);
        section.set("ARROW_INFINITE", 1);
        section.set(Setting.APPLY_TEXT.node(), "");
        
        return section;
    }

    public class SkillSpoutListener implements Listener {

        private final Skill skill;

        public SkillSpoutListener(Skill skill) {
            this.skill = skill;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInventoryEnchant(InventoryEnchantEvent event) {
            if (event.isCancelled()) {
                return;
            }

            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect(getName())) {
                Messaging.send(event.getPlayer(), "You don't have the skill to enchant an item!");
                Util.syncInventory(event.getPlayer(), plugin);
                event.setCancelled(true);
                return;
            }

            double xpCost = 0;
            List<String> enchants = SkillConfigManager.getUseSettingKeys(hero, skill);
            Map<Enchantment, Integer> newEnchants = event.getResult().getEnchantments();
            for (Enchantment enchant : newEnchants.keySet()) {
                if (event.getBefore().containsEnchantment(enchant)) {
                    continue;
                }
                if (!enchants.contains(enchant.getName())) {
                    event.setCancelled(true);
                    Util.syncInventory(event.getPlayer(), plugin);
                    return;
                }
                int level = SkillConfigManager.getUseSetting(hero, skill, enchant.getName(), 1, true);
                if (hero.getLevel(hero.getEnchantingClass()) < level) {
                    event.setCancelled(true);
                    Util.syncInventory(event.getPlayer(), plugin);
                    return;
                }
                xpCost += event.getResult().getEnchantmentLevel(enchant);
            }

            xpCost *= Heroes.properties.enchantXPMultiplier;
            event.setLevelAfter(event.getLevelBefore());
            hero.gainExp(-xpCost, ExperienceType.ENCHANTING);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInventoryOpen(InventoryOpenEvent event) {
            if (event.isCancelled() || !(((CraftPlayer) event.getPlayer()).getHandle().activeContainer instanceof ContainerEnchantTable))
                return;

            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect(getName())) {
                event.setCancelled(true);
                Messaging.send(event.getPlayer(), "You don't have the ability to enchant items!");
            }

            HeroClass hc = hero.getEnchantingClass();
            if (hc != null) {
                hero.syncExperience(hc);
                hero.setEnchanting(true);
            } else {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (hero.hasEffect(getName()) && hero.isEnchanting()) {
                hero.setEnchanting(false);
            }
        }
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
