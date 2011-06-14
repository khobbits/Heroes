package com.herocraftonline.dev.heroes.skill;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

public abstract class ActiveEffectSkill extends ActiveSkill {

    public final String SETTING_EXPIRETEXT = "expire-text";
    public final String SETTING_DURATION = "effect-duration";

    protected String expireText = null;

    public ActiveEffectSkill(Heroes plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        useText = getSetting(null, SETTING_USETEXT, "%hero% gained %skill%!");
        useText = useText.replace("%hero%", "$1").replace("%skill%", "$2");
        expireText = getSetting(null, SETTING_EXPIRETEXT, "%hero% lost %skill%!");
        expireText = expireText.replace("%hero%", "$1").replace("%skill%", "$2");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty(SETTING_USETEXT, "%hero% gained %skill%!");
        node.setProperty(SETTING_EXPIRETEXT, "%hero% lost %skill%!");
        node.setProperty(SETTING_DURATION, 10000d);
        return node;
    }

    protected void applyEffect(Hero hero) {
        hero.getEffects().putEffect(name, getSetting(hero.getHeroClass(), SETTING_DURATION, 10000d));
    }

    public void onExpire(Hero hero) {
        notifyNearbyPlayers(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getName(), name);
    }
}
