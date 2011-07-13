package com.herocraftonline.dev.heroes.skill;

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

    public static final String SETTING_APPLYTEXT = "apply-text";
    public static final String SETTING_UNAPPLYTEXT = "unapply-text";

    private String applyText = null;
    private String unapplyText = null;

    public PassiveSkill(Heroes plugin) {
        super(plugin);
        setUsage("Passive Skill");

        registerEvent(Type.CUSTOM_EVENT, new SkillCustomEventListener(), Priority.Monitor);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    protected void apply(Hero hero) {
        hero.applyEffect(getName(), -1);
        notifyNearbyPlayers(hero.getPlayer().getLocation(), applyText, hero.getPlayer().getName(), getName());
    }

    protected void unapply(Hero hero) {
        Long effect = hero.removeEffect(getName());
        if (effect != null) {
            notifyNearbyPlayers(hero.getPlayer().getLocation(), unapplyText, hero.getPlayer().getName(), getName());
        }
    }

    public void tryApplying(Hero hero) {
        HeroClass heroClass = hero.getHeroClass();
        if (!heroClass.hasSkill(getName())) {
            return;
        }
        ConfigurationNode settings = heroClass.getSkillSettings(getName());
        if (settings != null) {
            if (hero.getLevel() >= getSetting(heroClass, SETTING_LEVEL, 1)) {
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
