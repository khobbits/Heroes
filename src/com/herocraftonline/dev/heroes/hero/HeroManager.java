package com.herocraftonline.dev.heroes.hero;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainManaEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.CommandHandler;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.HeroStorage;
import com.herocraftonline.dev.heroes.persistence.YMLHeroStorage;
import com.herocraftonline.dev.heroes.skill.DelayedSkill;
import com.herocraftonline.dev.heroes.skill.OutsourcedSkill;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Player management
 *
 * @author Herocraft's Plugin Team
 */
public class HeroManager {

    private Heroes plugin;
    private Map<String, Hero> heroes;
    private HeroStorage heroStorage;
    private final static int manaInterval = 5;
    private final static int warmupInterval = 5;
    private Map<Hero, DelayedSkill> delayedSkills;
    private List<Hero> completedSkills;

    public HeroManager(Heroes plugin) {
        this.plugin = plugin;
        this.heroes = new HashMap<String, Hero>();
        // if (plugin.getConfigManager().getProperties().storageType.toLowerCase().equals("yml"))
        heroStorage = new YMLHeroStorage(plugin);

        int regenAmount = Heroes.properties.manaRegenPercent;
        long regenInterval = Heroes.properties.manaRegenInterval * 1000L;
        Runnable manaTimer = new ManaUpdater(this, regenInterval, regenAmount);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, manaTimer, 0, manaInterval);

        delayedSkills = new HashMap<Hero, DelayedSkill>();
        completedSkills = new ArrayList<Hero>();
        Runnable delayedExecuter = new DelayedSkillExecuter(this);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, delayedExecuter, 0, warmupInterval);

    }

    public void addHero(Hero hero) {
        heroes.put(hero.getPlayer().getName().toLowerCase(), hero);
    }

    public boolean containsPlayer(Player player) {
        return getHero(player) != null;
    }

    /**
     * Gets a hero Object from the hero mapping, if the hero does not exist then it loads in the Hero object for the
     * player
     *
     * @param player
     * @return
     */
    public Hero getHero(Player player) {
        Heroes.debug.startTask("HeroManager.getHero");
        String key = player.getName().toLowerCase();
        Hero hero = heroes.get(key);
        if (hero != null) {
            if (hero.getPlayer().getEntityId() != player.getEntityId()) {
                heroes.remove(key);
                hero.clearEffects();
                saveHero(hero, true);
            } else {
                Heroes.debug.stopTask("HeroManager.getHero");
                return hero;
            }
        }

        // If it gets to this stage then clearly the HeroManager doesn't have it so we create it...
        hero = heroStorage.loadHero(player);
        addHero(hero);
        performSkillChecks(hero);

        Heroes.debug.stopTask("HeroManager.getHero");
        return hero;
    }

    public void checkClasses(Hero hero) {
        Player player = hero.getPlayer();
        HeroClass playerClass = hero.getHeroClass();
        HeroClass secondClass = hero.getSecondClass();
        if (!CommandHandler.hasPermission(player, "heroes.classes." + playerClass.getName().toLowerCase())) {
            hero.setHeroClass(plugin.getClassManager().getDefaultClass(), false);
        }

        if (secondClass != null && !CommandHandler.hasPermission(player, "heroes.classes." + secondClass.getName().toLowerCase())) {
            hero.setHeroClass(null, true);
        }
    }

    public Collection<Hero> getHeroes() {
        return Collections.unmodifiableCollection(heroes.values());
    }

    public void performSkillChecks(Hero hero) {
        for (Skill skill : plugin.getSkillManager().getSkills()) {
            if (skill instanceof OutsourcedSkill) {
                ((OutsourcedSkill) skill).tryLearningSkill(hero);
            } else if (skill instanceof PassiveSkill) {
                ((PassiveSkill) skill).tryApplying(hero);
            }
        }
    }

    public void removeHero(Hero hero) {
        if (hero != null) {
            if (hero.hasParty()) {
                HeroParty party = hero.getParty();
                party.removeMember(hero);
                if (party.getMembers().size() == 0) {
                    this.plugin.getPartyManager().removeParty(party);
                }
            }
            // If we are removing the hero object we should remove it from everywhere to make sure it's not staying in memory.
            completedSkills.remove(hero);
            delayedSkills.remove(hero);
            heroes.remove(hero.getName().toLowerCase());
        }
    }

    /**
     * Save the given Players Data to a file.
     *
     * @param player
     */
    public void saveHero(Hero hero, boolean now) {
        heroStorage.saveHero(hero, now);
        Heroes.log(Level.INFO, "Saved hero: " + hero.getPlayer().getName());

    }

    public void saveHero(Player player, boolean now) {
        saveHero(getHero(player), now);
    }

    public void stopTimers() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    public Map<Hero, DelayedSkill> getDelayedSkills() {
        return delayedSkills;
    }

    public List<Hero> getCompletedSkills() {
        return completedSkills;
    }

    public void addCompletedSkill(Hero hero) {
        completedSkills.add(hero);
    }
}

class ManaUpdater implements Runnable {

    private final HeroManager manager;
    private final long updateInterval;
    private final int manaPercent;
    private long lastUpdate = 0;

    ManaUpdater(HeroManager manager, long updateInterval, int manaPercent) {
        this.manager = manager;
        this.updateInterval = updateInterval;
        this.manaPercent = manaPercent;
    }

    @Override
    public void run() {
        Heroes.debug.startTask("ManaUpdater.run");
        long time = System.currentTimeMillis();
        if (time < lastUpdate + updateInterval) {
            Heroes.debug.stopTask("ManaUpdater.run");
            return;
        }
        lastUpdate = time;

        Collection<Hero> heroes = manager.getHeroes();
        for (Hero hero : heroes) {
            if (hero == null) {
                continue;
            }

            int mana = hero.getMana();
            if (mana == 100) {
                continue;
            }

            HeroRegainManaEvent hrmEvent = new HeroRegainManaEvent(hero, manaPercent, null);
            Bukkit.getServer().getPluginManager().callEvent(hrmEvent);
            if (hrmEvent.isCancelled()) {
                continue;
            }

            hero.setMana(mana + hrmEvent.getAmount());
            if (hero.isVerbose()) {
                Messaging.send(hero.getPlayer(), ChatColor.BLUE + "MANA " + Messaging.createManaBar(hero.getMana()));
            }
        }

        Heroes.debug.stopTask("ManaUpdater.run");
    }
}

class DelayedSkillExecuter implements Runnable {
    private final HeroManager manager;

    DelayedSkillExecuter(HeroManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        Heroes.debug.startTask("WarmupExecuter.run");
        //Cleanup already finished skills
        for (Hero hero : manager.getCompletedSkills()) {
            manager.getDelayedSkills().remove(hero);
            hero.setDelayedSkill(null);
        }
        manager.getCompletedSkills().clear();

        Iterator<Entry<Hero, DelayedSkill>> iter = manager.getDelayedSkills().entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Hero, DelayedSkill> entry = iter.next();
            if (entry.getKey().getDelayedSkill() == null) {
                iter.remove();
            } else if (entry.getValue().isReady()) {
                DelayedSkill dSkill = entry.getValue();
                try {
                    dSkill.getSkill().execute(dSkill.getPlayer(), dSkill.getIdentifier(), dSkill.getArgs());
                } catch (Exception e) {
                    // Cleanup the player from the still casting stuff
                    Hero hero = entry.getKey();
                    hero.removeEffect(hero.getEffect("Casting"));
                    hero.setDelayedSkill(null);
                    manager.addCompletedSkill(hero);
                    Heroes.log(Level.SEVERE, "There was an error executing: " + dSkill.getSkill().getName() + " for " + hero.getPlayer().getName());
                    System.out.println(e);
                }
            }
        }
        Heroes.debug.stopTask("WarmupExecuter.run");
    }
}
