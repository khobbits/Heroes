package com.herocraftonline.dev.heroes;

import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class HEventListener extends HeroesEventListener {

    private Heroes plugin;

    public HEventListener(Heroes heroes) {
        this.plugin = heroes;
    }

    @Override
    public void onHeroChangeLevel(HeroChangeLevelEvent event) {
        Hero hero = event.getHero();
        HeroClass heroClass = hero.getHeroClass();

        int level = event.getTo();
        if (level > event.getFrom()) {
            for (Skill skill : plugin.getSkillManager().getSkills()) {
                if (heroClass.hasSkill(skill.getName())) {
                    int levelRequired = skill.getSetting(heroClass, Setting.LEVEL.node(), 1);
                    if (levelRequired == level) {
                        Messaging.send(event.getHero().getPlayer(), "You have learned $1.", skill.getName());
                    }
                }
            }
        } else {
            for (Skill skill : plugin.getSkillManager().getSkills()) {
                if (heroClass.hasSkill(skill.getName())) {
                    if (skill.getSetting(heroClass, Setting.LEVEL.node(), 1) > level) {
                        Messaging.send(event.getHero().getPlayer(), "You have forgotton how to use $1", skill.getName());
                    }
                }
            }
        }
    }

    @Override
    public void onHeroRegainHealth(HeroRegainHealthEvent event) {
        if (event.isCancelled() || !event.getHero().hasParty())
            return;

        HeroParty party = event.getHero().getParty();
        if (event.getAmount() > 0 && !party.updateMapDisplay()) {
            party.setUpdateMapDisplay(true);
        }
    }
}
