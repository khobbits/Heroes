package com.herocraftonline.dev.heroes.command.skill;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public abstract class ActiveSkill extends Skill {

    public final String SETTING_MANA = "mana";
    public final String SETTING_COOLDOWN = "cooldown";
    public final String SETTING_EXP = "exp";
    public final String SETTING_USETEXT = "use-text";

    protected String useText;
    protected boolean awardExpOnCast = true;

    public ActiveSkill(Heroes plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        useText = getSetting(null, SETTING_USETEXT, "%hero% used %skill%!");
        useText = useText.replace("%hero%", "$1").replace("%skill%", "$2");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty(SETTING_USETEXT, "%hero% used %skill%!");
        return node;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero == null) {
                Messaging.send(player, "You are not a hero.");
                return;
            }
            HeroClass heroClass = hero.getHeroClass();
            if (!heroClass.hasSkill(name) && !heroClass.hasSkill("*")) {
                Messaging.send(player, "$1s cannot use $2.", heroClass.getName(), name);
                return;
            }
            int level = getSetting(heroClass, SETTING_LEVEL, 1);
            if (!meetsLevelRequirement(hero, level)) {
                Messaging.send(player, "You must be level $1 to use $2.", String.valueOf(level), name);
                return;
            }
            int manaCost = getSetting(heroClass, SETTING_MANA, 0);
            if (manaCost > hero.getMana()) {
                Messaging.send(player, "Not enough mana!");
                return;
            }
            Map<String, Long> cooldowns = hero.getCooldowns();
            long time = System.currentTimeMillis();
            int cooldown = getSetting(heroClass, SETTING_COOLDOWN, 0);
            if (cooldown > 0) {
                Long timeUsed = cooldowns.get(name);
                if (timeUsed != null) {
                    if (time < timeUsed + cooldown) {
                        long remaining = timeUsed + cooldown - time;
                        Messaging.send(hero.getPlayer(), "Sorry, $1 still has $2 seconds left on cooldown!", name, Long.toString(remaining / 1000));
                        return;
                    }
                }
            }
            if (use(hero, args)) {
                if (cooldown > 0) {
                    cooldowns.put(name, time);
                }

                if (this.awardExpOnCast) {
                    this.awardExp(hero);
                }

                hero.setMana(hero.getMana() - manaCost);
                if (hero.isVerbose() && manaCost > 0) {
                    Messaging.send(hero.getPlayer(), Messaging.createManaBar(hero.getMana()));
                }
            }
        }
    }

    private void awardExp(Hero hero) {
        HeroClass heroClass = hero.getHeroClass();
        if (heroClass.getExperienceSources().contains(ExperienceType.SKILL)) {
            hero.gainExp(this.getSetting(heroClass, this.SETTING_EXP, 0), ExperienceType.SKILL);
        }
    }

    public abstract boolean use(Hero hero, String[] args);

}
