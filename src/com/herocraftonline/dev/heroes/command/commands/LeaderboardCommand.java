package com.herocraftonline.dev.heroes.command.commands;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class LeaderboardCommand extends BasicCommand {
    private final Heroes plugin;

    public LeaderboardCommand(Heroes plugin) {
        super("Leaderboard");
        this.plugin = plugin;
        setDescription("Displays Hero rankings");
        setUsage("/hero leaderboard");
        setArgumentRange(0, 0);
        setIdentifiers("hero leaderboard");
        setPermission("heroes.leaderboard");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Collection<Hero> heroes = plugin.getHeroManager().getHeroes();
        Map<Hero, Double> leaderboard = new TreeMap<Hero, Double>(new Comparator<Hero>() {
            @Override
            public int compare(Hero h1, Hero h2) {
                return (int) (h1.getExperience(h1.getHeroClass()) - h2.getExperience(h2.getHeroClass()));
            }
        });

        for (Hero hero : heroes) {
            if (hero != null) {
                leaderboard.put(hero, hero.getExperience(hero.getHeroClass()));
            }
        }

        Iterator<Entry<Hero, Double>> iter = leaderboard.entrySet().iterator();
        for (int i = 0; i < 5 && iter.hasNext(); i++) {
            Entry<Hero, Double> entry = iter.next();
            Messaging.send(sender, "$1 - $2", entry.getKey().getPlayer().getName(), entry.getValue());
        }

        return true;
    }
}
