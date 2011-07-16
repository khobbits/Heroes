package com.herocraftonline.dev.heroes.damage;

import java.util.HashMap;

import com.herocraftonline.dev.heroes.Heroes;

public class HeroesDamage {
    public Heroes plugin;
    private HashMap<Integer, Double> mobHealthValues = new HashMap<Integer, Double>();

    public HeroesDamage(Heroes plugin) {
        this.plugin = plugin;
    }

    public void addMonster(Integer entityID, Double hp) {
        mobHealthValues.put(entityID, hp);
    }

    /**
     * @return the mobHealthValues
     */
    public HashMap<Integer, Double> getMobHealthValues() {
        return mobHealthValues;
    }

    public void registerEvents() {

    }

}
