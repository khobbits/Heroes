package com.herocraftonline.dev.heroes.command.skill;

import org.bukkit.command.CommandSender;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.api.LevelEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;

public abstract class PassiveSkill extends Skill {

    public final String SETTING_APPLYTEXT = "apply-text";
    public final String SETTING_UNAPPLYTEXT = "unapply-text";

    protected String applyText = null;
    protected String unapplyText = null;

    public PassiveSkill(Heroes plugin) {
        super(plugin);
        usage = "Passive Skill";

        registerEvent(Type.CUSTOM_EVENT, new SkillCustomEventListener(), Priority.Monitor);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    protected void apply(Hero hero) {
        hero.getEffects().putEffect(name, Double.POSITIVE_INFINITY);
        notifyNearbyPlayers(hero.getPlayer().getLocation(), applyText, hero.getPlayer().getName(), name);
    }

    protected void unapply(Hero hero) {
        Double effect = hero.getEffects().removeEffect(name.toLowerCase());
        if (effect != null) {
            notifyNearbyPlayers(hero.getPlayer().getLocation(), unapplyText, hero.getPlayer().getName(), name);
        }
    }

    public void tryApplying(Hero hero) {
        HeroClass heroClass = hero.getHeroClass();
        if (!heroClass.hasSkill(name)) {
            return;
        }
        ConfigurationNode settings = heroClass.getSkillSettings(name);
        if (settings != null) {
            if (meetsLevelRequirement(hero, getSetting(heroClass, SETTING_LEVEL, 1))) {
                apply(hero);
            } else {
                unapply(hero);
            }
        }
    }

    @Override
    public void init() {
        applyText = getSetting(null, SETTING_APPLYTEXT, "%hero% gained %skill%!");
        applyText = applyText.replace("%hero%", "$1").replace("%skill%", "$2");
        unapplyText = getSetting(null, SETTING_UNAPPLYTEXT, "%hero% lost %skill%!");
        unapplyText = unapplyText.replace("%hero%", "$1").replace("%skill%", "$2");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty(SETTING_APPLYTEXT, "%hero% gained %skill%!");
        node.setProperty(SETTING_UNAPPLYTEXT, "%hero% lost %skill%!");
        return node;
    }

    public class SkillCustomEventListener extends CustomEventListener {

        @Override
        public void onCustomEvent(Event event) {
            if (event instanceof LevelEvent) {
                LevelEvent subEvent = (LevelEvent) event;
                if (!subEvent.isCancelled()) {
                    tryApplying(subEvent.getHero());
                }
            } else if (event instanceof ClassChangeEvent) {
                ClassChangeEvent subEvent = (ClassChangeEvent) event;
                if (subEvent.isCancelled()) {
                    tryApplying(subEvent.getHero());
                }
            }
        }
    }

}
