package com.herocraftonline.dev.heroes.damage;

import java.util.HashMap;

import com.herocraftonline.dev.heroes.Heroes;

public class HeroesDamage {
    public Heroes plugin;
    private HashMap<Integer, Integer> mobHealthValues = new HashMap<Integer, Integer>();

    public HeroesDamage(Heroes plugin) {
        this.plugin = plugin;
    }

    public void addMonster(Integer entityID, Integer hp) {
        mobHealthValues.put(entityID, hp);
    }

    /**
     * @return the mobHealthValues
     */
    public HashMap<Integer, Integer> getMobHealthValues() {
        return mobHealthValues;
    }

    public void registerEvents() {

    }

}
