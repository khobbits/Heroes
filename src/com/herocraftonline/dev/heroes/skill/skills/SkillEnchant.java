package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillEnchant extends PassiveSkill {

    public SkillEnchant(Heroes plugin) {
        super(plugin, "Enchant");

        setDescription("You are able to enchant items.");
        setArgumentRange(0, 0);
        setTypes(SkillType.KNOWLEDGE, SkillType.ITEM);
        setEffectTypes(EffectType.BENEFICIAL);

        if (Heroes.useSpout()) {
            Bukkit.getServer().getPluginManager().registerEvents(new SkillEnchantListener(this), plugin);
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
        section.set("enchant-level-mult", 2.0);
        return section;
    }

    public class SkillEnchantListener implements Listener {

        private final Skill skill;

        public SkillEnchantListener(Skill skill) {
            this.skill = skill;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
            if (event.isCancelled()) {
                return;
            }
            Hero hero = plugin.getHeroManager().getHero(event.getEnchanter());
            if (!hero.hasEffect(getName())) {
                // Don't offer enchants to players that don't meet the requirements
                event.setCancelled(true);
                return;
            }
            HeroClass hc = hero.getEnchantingClass();
            if (hc != null) {
                hero.syncExperience(hc);
                hero.setEnchanting(true);
            } else {
                // if for some reason we don't have an enchanting class also cancel the event
                event.setCancelled(true);
                return;
            }
            double mult = SkillConfigManager.getUseSetting(hero, skill, "enchant-level-mult", 2.0, false);
            for (int i = 0; i < event.getExpLevelCostsOffered().length; i++) {
                event.getExpLevelCostsOffered()[i] = (int) (event.getExpLevelCostsOffered()[i] * mult);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onEnchantItem(EnchantItemEvent event) {
            Hero hero = plugin.getHeroManager().getHero(event.getEnchanter());
            if (event.isCancelled()) {
                hero.setEnchanting(false);
                return;
            }
            int level = hero.getLevel(hero.getEnchantingClass());
            event.setExpLevelCost(0);
            Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
            Iterator<Entry<Enchantment, Integer>> iter = enchants.entrySet().iterator();
            int xpCost = 0;
            while (iter.hasNext()) {
                Entry<Enchantment, Integer> entry = iter.next();
                int reqLevel = SkillConfigManager.getUseSetting(hero, skill, entry.getKey().getName(), 1, true);
                if (level < reqLevel) {
                    iter.remove();
                } else {
                    xpCost += entry.getValue();
                }
            }
            if (xpCost == 0) {
                event.setCancelled(true);
            } else {
                xpCost *= Heroes.properties.enchantXPMultiplier;
                hero.gainExp(-xpCost, ExperienceType.ENCHANTING);
            }
            hero.setEnchanting(false);
        }
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
