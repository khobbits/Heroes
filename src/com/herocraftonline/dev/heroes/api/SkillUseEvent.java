package com.herocraftonline.dev.heroes.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

@SuppressWarnings("serial")
public class SkillUseEvent extends Event implements Cancellable {

    private final Player player;
    private final Skill skill;
    private final Hero hero;
    private String[] args;
    private boolean cancelled = false;

    public SkillUseEvent(Skill skill, Player player, Hero hero, String[] args) {
        super("SkillUseEvent");
        this.player = player;
        this.skill = skill;
        this.hero = hero;
        this.args = args;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean val) {
        cancelled = val;
    }

}
