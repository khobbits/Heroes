package com.herocraftonline.dev.heroes.command.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class LeaderboardCommand extends BaseCommand{

    public LeaderboardCommand(Heroes plugin) {
        super(plugin);
        name = "Leaderboard";
        description = "Checks the online players and returns the 5 highest";
        usage = "/hero leaderboard";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("hero leaderboard");
        this.permissionNode = "heroes.leaderboard";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(CommandSender sender, String[] args) {
        Hero[] heroList = plugin.getHeroManager().getHeroes();
        HashMap<Hero, Integer> heroValues = new HashMap<Hero, Integer>();
        int i = 0;
        for(Hero hero : heroList){
            heroValues.put(hero, hero.getExperience());
        }
        heroValues = (HashMap<Hero, Integer>) sortByValue(heroValues);
        for(Hero hero : heroValues.keySet()){
            i++;
            if(i >= (heroValues.size() - 5)){
                Player player = hero.getPlayer();
                Messaging.send(sender, "$1 - $2", player.getName(), Integer.toString(hero.getExperience()));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
