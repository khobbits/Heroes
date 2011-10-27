package com.herocraftonline.dev.heroes.persistence;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;

public abstract class HeroStorage {

    protected Heroes plugin;

    public HeroStorage(Heroes plugin) {
        this.plugin = plugin;
    }

    public Hero createNewHero(Player player) {
        Hero hero = new Hero(plugin, player, plugin.getClassManager().getDefaultClass(), null);
        hero.setMana(100);
        hero.setHealth(hero.getMaxHealth());
        hero.syncHealth();
        return hero;
    }

    public abstract Hero loadHero(Player player);

    public abstract boolean saveHero(Hero hero);
}
