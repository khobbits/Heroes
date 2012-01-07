package com.herocraftonline.dev.heroes.hero;

import com.herocraftonline.dev.heroes.Heroes
import com.herocraftonline.dev.heroes.classes.HeroClass
import com.herocraftonline.dev.heroes.util.Properties
import org.bukkit.entity.Player
import spock.lang.Specification

class HeroTest extends Specification {

    def "test xp loss calculation"() {
        given:
        Heroes plugin = Mock(Heroes.class)
        Player player = Mock(Player.class)
        HeroClass heroClass = Mock(HeroClass.class)
        Hero hero = new Hero(plugin, player, heroClass, null)
        hero.setExperience(heroClass, initial)
        Properties.maxLevel = 10
        Properties.maxExp = 1000000000
        Properties.levels = new int[10]
        for (int i = 0; i < 10; i++) {
            Properties.levels[i] = Math.pow(10, i);
        }

        expect:
        hero.calculateXPLoss(multipler, heroClass) == loss

        where:
        initial | multipler | loss
        10000   | 0.5       | 4500
        10000   | 1.5       | 9450
        10000   | 3.0       | 9990
        10000   | 3.5       | 9994.5
        55000   | 2.5       | 54900
    }

}