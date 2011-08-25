package com.herocraftonline.dev.heroes;

import java.util.List;

import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
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
    public void onHeroChangeLevel(HeroChangeLevelEvent event) {
        Hero hero = event.getHero();
        HeroClass heroClass = hero.getHeroClass();
        hero.syncHealth();

        int level = event.getTo();
        List<Command> sortCommands = plugin.getCommandHandler().getCommands();
        if (level > event.getFrom()) {
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
        } else {
            for (String skillName : heroClass.getSkillNames()) {
                Skill skill = (Skill) plugin.getCommandHandler().getCommand(skillName);
                if (skill.getSetting(heroClass, "level", 1) > level) {
                    Messaging.send(event.getHero().getPlayer(), "You have forgotton how to use $1", skillName);
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
