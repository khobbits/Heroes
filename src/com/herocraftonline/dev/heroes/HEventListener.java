package com.herocraftonline.dev.heroes;

import java.util.List;

import com.herocraftonline.dev.heroes.api.HeroLevelEvent;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillUseEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.Command;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HEventListener extends HeroesEventListener {

    private Heroes plugin;

    public HEventListener(Heroes heroes) {
        this.plugin = heroes;
    }

    @Override
    public void onHeroLevel(HeroLevelEvent event) {
        Hero hero = event.getHero();
        HeroClass heroClass = hero.getHeroClass();
        hero.syncHealth();

        int level = event.getTo();
        List<Command> sortCommands = plugin.getCommandHandler().getCommands();
        for (Command command : sortCommands) {
            if (command instanceof Skill) {
                Skill skill = (Skill) command;
                if (heroClass.hasSkill(skill.getName())) {
                    int levelRequired = skill.getSetting(heroClass, "level", 1);
                    if (levelRequired == level) {
                        Messaging.send(event.getHero().getPlayer(), "You have learned $1.", skill.getName());
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
