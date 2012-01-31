package com.herocraftonline.dev.heroes;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.api.SkillUseEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class HEventListener implements Listener {

    private Heroes plugin;

    public HEventListener(Heroes heroes) {
        this.plugin = heroes;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeroChangeLevel(HeroChangeLevelEvent event) {
        Hero hero = event.getHero();
        HeroClass heroClass = event.getHeroClass();

        int level = event.getTo();
        if (level > event.getFrom()) {
            for (Skill skill : plugin.getSkillManager().getSkills()) {
                if (heroClass.hasSkill(skill.getName()) && hero.canUseSkill(skill)) {
                    int levelRequired = SkillConfigManager.getUseSetting(hero, skill, Setting.LEVEL, 1, true);
                    if (levelRequired == level) {
                        Messaging.send(event.getHero().getPlayer(), "You have learned $1.", skill.getName());
                    }
                }
            }
        } else {
            for (Skill skill : plugin.getSkillManager().getSkills()) {
                if (heroClass.hasSkill(skill.getName())) {
                    int levelRequired = SkillConfigManager.getUseSetting(hero, skill, Setting.LEVEL, 1, true);
                    if (levelRequired > level && levelRequired <= event.getFrom()) {
                        Messaging.send(event.getHero().getPlayer(), "You have forgotton how to use $1", skill.getName());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeroRegainHealth(HeroRegainHealthEvent event) {
        if (event.isCancelled() || !event.getHero().hasParty())
            return;

        HeroParty party = event.getHero().getParty();
        if (event.getAmount() > 0) {
            party.update();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSkillUse(SkillUseEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String worldName = event.getPlayer().getWorld().getName();
        if (Heroes.properties.disabledWorlds.contains(worldName)) {
            Messaging.send(event.getPlayer(), "Skills have been disabled on this world!");
            event.setCancelled(true);
            return;
        }

        Hero hero = event.getHero();
        if (hero.hasEffect("Root") && event.getSkill().isType(SkillType.MOVEMENT) && !event.getSkill().isType(SkillType.COUNTER)) {
            Messaging.send(hero.getPlayer(), "You can't use that skill while rooted!");
            event.setCancelled(true);
        }

        if (hero.hasEffectType(EffectType.SILENCE) && event.getSkill().isType(SkillType.SILENCABLE)) {
            Messaging.send(hero.getPlayer(), "You can't use that skill while silenced!");
            event.setCancelled(true);
        } else if (hero.hasEffectType(EffectType.STUN) || hero.hasEffectType(EffectType.DISABLE)) {
            if (!event.getSkill().isType(SkillType.COUNTER)) {
                event.setCancelled(true);
            }
        }
    }
}
