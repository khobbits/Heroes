package com.herocraftonline.dev.heroes.hero;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.party.PartyManager;
import com.herocraftonline.dev.heroes.persistence.HeroStorage;
import com.herocraftonline.dev.heroes.persistence.YMLHeroStorage;
import com.herocraftonline.dev.heroes.ui.MapAPI;
import com.herocraftonline.dev.heroes.ui.MapInfo;
import com.herocraftonline.dev.heroes.ui.TextRenderer;
import com.herocraftonline.dev.heroes.ui.TextRenderer.CharacterSprite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainManaEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.Expirable;
import com.herocraftonline.dev.heroes.effects.Periodic;
import com.herocraftonline.dev.heroes.skill.OutsourcedSkill;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

/**
 * Player management
 * 
 * @author Herocraft's Plugin Team
 */
public class HeroManager {

    protected Heroes plugin;
    private Set<Hero> heroes;
    protected Map<Creature, Set<Effect>> creatureEffects;
    private HeroStorage heroStorage;
    private final static int effectInterval = 2;
    private final static int manaInterval = 5;
    private final static int partyUpdateInterval = 5;

    public HeroManager(Heroes plugin) {
        this.plugin = plugin;
        this.heroes = new HashSet<Hero>();
        this.creatureEffects = new HashMap<Creature, Set<Effect>>();

        // if (plugin.getConfigManager().getProperties().storageType.toLowerCase().equals("yml"))
        heroStorage = new YMLHeroStorage(plugin);

        Runnable effectTimer = new EffectUpdater(this);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, effectTimer, 0, effectInterval);

        int regenAmount = plugin.getConfigManager().getProperties().manaRegenPercent;
        long regenInterval = plugin.getConfigManager().getProperties().manaRegenInterval * 1000L;
        Runnable manaTimer = new ManaUpdater(this, regenInterval, regenAmount);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, manaTimer, 0, manaInterval);

        Runnable partyUpdater = new PartyUpdater(this, plugin, plugin.getPartyManager());
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, partyUpdater, 0, partyUpdateInterval);
    }

    public boolean addHero(Hero hero) {
        return heroes.add(hero);
    }

    public boolean containsPlayer(Player player) {
        return getHero(player) != null;
    }

    /**
     * Gets a hero Object from the hero mapping, if the hero does not exist then it loads in the Hero object for the player
     * 
     * @param player
     * @return
     */
    public Hero getHero(Player player) {
        for (Hero hero : getHeroes()) {
            if (hero == null || hero.getPlayer() == null) {
                removeHero(hero); // Seeing as it's null we might as well remove it.
                continue;
            }
            if (player.getName().equalsIgnoreCase(hero.getPlayer().getName())) {
                //If the entity ID's don't match for some reason then the player object is invalid and we need to re-load the hero object
                if (hero.getPlayer().getEntityId() != player.getEntityId()) {
                    removeHero(hero);
                    break;
                }
                return hero;
            }
        }
        // If it gets to this stage then clearly the HeroManager doesn't have it so we create it...
        Hero hero = heroStorage.loadHero(player);
        addHero(hero);
        performSkillChecks(hero);
        return hero;
    }

    public Set<Hero> getHeroes() {
        return new HashSet<Hero>(heroes);
    }
    
    public boolean removeHero(Hero hero) {
        if (hero != null && hero.hasParty()) {
            HeroParty party = hero.getParty();
            party.removeMember(hero);
            if (party.getMembers().size() == 0) {
                this.plugin.getPartyManager().removeParty(party);
            }
        }

        return heroes.remove(hero);
    }

    /**
     * Save the given Players Data to a file.
     * 
     * @param player
     */
    public void saveHero(Hero hero) {
        if (heroStorage.saveHero(hero))
            Heroes.log(Level.INFO, "Saved hero: " + hero.getPlayer().getName());
    }
    
    public void saveHero(Player player) {
        saveHero(getHero(player));
    }

    public void stopTimers() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    HashMap<Creature, Set<Effect>> getCreatureEffects() {
        return new HashMap<Creature, Set<Effect>>(creatureEffects);
    }

    public void performSkillChecks(Hero hero) {
        HeroClass playerClass = hero.getHeroClass();

        for (Skill skill : plugin.getSkillManager().getSkills()) {
            // Never try to learn * or ALL as skills, can happen if the nodes are added incorrectly
            if (skill.getName().equals("*") || skill.getName().toLowerCase().equals("ALL"))
                continue;

            if (skill instanceof OutsourcedSkill) {
                if (playerClass.hasSkill(skill.getName())) {
                    ((OutsourcedSkill) skill).tryLearningSkill(hero);
                }
            }
            if (skill instanceof PassiveSkill) {
                if (playerClass.hasSkill(skill.getName())) {
                    ((PassiveSkill) skill).tryApplying(hero);
                }
            }
        }
    }

    /**
     * Adds a new effect to the specific creature
     * 
     * @param creature
     * @param effect
     */
    public void addCreatureEffect(Creature creature, Effect effect) {
        Set<Effect> cEffects = creatureEffects.get(creature);
        if (cEffects == null) {
            cEffects = new HashSet<Effect>();
            creatureEffects.put(creature, cEffects);
        }
        cEffects.add(effect);
        effect.apply(creature);
    }

    /**
     * Removes an effect from a creature
     * 
     * @param creature
     * @param effect
     */
    public void removeCreatureEffect(Creature creature, Effect effect) {
        Set<Effect> cEffects = creatureEffects.get(creature);
        if (cEffects != null) {
            effect.remove(creature);
            cEffects.remove(effect);
            // If the creature has no effects left
            if (cEffects.isEmpty()) {
                creatureEffects.remove(creature);
            }
        }
    }

    /**
     * Clears all effects from the creature
     * 
     * @param creature
     */
    public void clearCreatureEffects(Creature creature) {
        if (creatureEffects.containsKey(creature)) {
            Iterator<Effect> iter = creatureEffects.get(creature).iterator();
            while (iter.hasNext()) {
                iter.next().remove(creature);
                iter.remove();
            }
            creatureEffects.remove(creature);
        }
    }

    /**
     * Checks if a creature has the effect
     * 
     * @param creature
     * @param effect
     * @return
     */
    public boolean creatureHasEffect(Creature creature, String name) {
        if (!creatureEffects.containsKey(creature))
            return false;
        for (Effect effect : creatureEffects.get(creature)) {
            if (effect.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a set view of all effects currently applied to the specified creature
     * 
     * @param creature
     * @return
     */
    public Set<Effect> getCreatureEffects(Creature creature) {
        return creatureEffects.get(creature);
    }

    public Effect getCreatureEffect(Creature creature, String name) {
        if (creatureEffects.get(creature) == null)
            return null;

        for (Effect effect : creatureEffects.get(creature)) {
            if (effect.getName().equals(name)) {
                return effect;
            }
        }
        return null;
    }
}

class EffectUpdater implements Runnable {

    private final HeroManager heroManager;

    EffectUpdater(HeroManager heroManager) {
        this.heroManager = heroManager;
    }

    public void run() {
        for (Hero hero : heroManager.getHeroes()) {
            for (Effect effect : hero.getEffects()) {
                if (effect instanceof Expirable) {
                    Expirable expirable = (Expirable) effect;
                    if (expirable.isExpired()) {
                        hero.removeEffect(effect);
                        continue;
                    }
                }

                if (effect instanceof Periodic) {
                    Periodic periodic = (Periodic) effect;
                    if (periodic.isReady()) {
                        periodic.tick(hero);
                    }
                }
            }
        }

        for (Entry<Creature, Set<Effect>> cEntry : heroManager.getCreatureEffects().entrySet()) {
            for (Effect effect : cEntry.getValue()) {
                if (effect instanceof Expirable) {
                    Expirable expirable = (Expirable) effect;
                    if (expirable.isExpired()) {
                        heroManager.removeCreatureEffect(cEntry.getKey(), effect);
                        continue;
                    }
                }
                if (effect instanceof Periodic) {
                    Periodic periodic = (Periodic) effect;
                    if (periodic.isReady()) {
                        periodic.tick(cEntry.getKey());
                    }
                }
            }
        }
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

    public void run() {
        long time = System.currentTimeMillis();
        if (time < lastUpdate + updateInterval) {
            return;
        }
        lastUpdate = time;

        Set<Hero> heroes = manager.getHeroes();
        for (Hero hero : heroes) {
            if (hero == null) {
                continue;
            }

            int mana = hero.getMana();
            if (mana == 100)
                continue;

            HeroRegainManaEvent hrmEvent = new HeroRegainManaEvent(hero, manaPercent, null);
            manager.plugin.getServer().getPluginManager().callEvent(hrmEvent);
            if (hrmEvent.isCancelled())
                continue;

            hero.setMana(mana + hrmEvent.getAmount());
            if (hero.isVerbose()) {
                Messaging.send(hero.getPlayer(), ChatColor.BLUE + "MANA " + Messaging.createManaBar(hero.getMana()));
            }
        }
    }
}

class PartyUpdater implements Runnable {

    private final HeroManager manager;
    private final Heroes plugin;
    private final PartyManager partyManager;

    PartyUpdater(HeroManager manager, Heroes plugin, PartyManager partyManager) {
        this.manager = manager;
        this.plugin = plugin;
        this.partyManager = partyManager;
    }

    public void run() {
        if (!this.plugin.getConfigManager().getProperties().mapUI)
            return;

        // System.out.print("Size - " + partyManager.getParties().size() + " Tick - " +
        // Bukkit.getServer().getWorlds().get(0).getTime());

        if (partyManager.getParties().size() == 0)
            return;

        for (HeroParty party : partyManager.getParties()) {
            if (party.updateMapDisplay()) {
                party.setUpdateMapDisplay(false);
                Player[] players = new Player[party.getMembers().size()];
                int count = 0;
                for (Hero heroes : party.getMembers()) {
                    players[count] = heroes.getPlayer();
                    count++;
                }
                updateMapView(players);
            }
        }
    }

    private void updateMapView(Player[] players) {
        MapAPI mapAPI = new MapAPI();
        short mapId = this.plugin.getConfigManager().getProperties().mapID;

        TextRenderer text = new TextRenderer(this.plugin);
        CharacterSprite sword = CharacterSprite.make("      XX", "     XXX", "    XXX ", "X  XXX  ", " XXXX   ", "  XX    ", " X X    ", "X   X   ");
        CharacterSprite crown = CharacterSprite.make("        ", "        ", "XX XX XX", "X XXXX X", "XX XX XX", " XXXXXX ", " XXXXXX ", "        ");
        CharacterSprite shield = CharacterSprite.make("   XX   ", "X  XX  X", "XXXXXXXX", "XXXXXXXX", "XXXXXXXX", " XXXXXX ", "  XXXX  ", "   XX   ");
        CharacterSprite heal = CharacterSprite.make("        ", "  XXX   ", "  XXX   ", "XXXXXXX ", "XXXXXXX ", "XXXXXXX ", "  XXX   ", "  XXX   ");
        CharacterSprite bow = CharacterSprite.make("XXXX   X", "X  XX X ", " X   X  ", "  X X X ", "   X  XX", "  X X  X", "XX   X X", " X    XX");
        text.setChar('\u0001', crown);
        text.setChar('\u0002', sword);
        text.setChar('\u0003', shield);
        text.setChar('\u0004', heal);
        text.setChar('\u0005', bow);

        MapInfo info = mapAPI.loadMap(Bukkit.getServer().getWorlds().get(0), mapId);
        mapAPI.getWorldMap(Bukkit.getServer().getWorlds().get(0), mapId).map = (byte) 9;

        info.setData(new byte[128 * 128]);

        String map = "§22;Party Members -\n";

        for (int i = 0; i < players.length; i++) {
            Hero hero = this.manager.getHero(players[i]);
            if (hero.getParty().getLeader().equals(hero)) {
                map += "§42;\u0001";
            } else {
                map += "§27;\u0002";
            }
            boolean damage = plugin.getConfigManager().getProperties().damageSystem;
            double currentHP;
            double maxHP;
            if (damage) {
                currentHP = hero.getHealth();
                maxHP = hero.getMaxHealth();
            } else {
                currentHP = hero.getPlayer().getHealth();
                maxHP = 20;
            }
            map += " §12;" + players[i].getName() + "\n" + createHealthBar(currentHP, maxHP) + "\n";
        }

        text.fancyRender(info, 10, 3, map);

        for (int i = 0; i < players.length; i++) {
            mapAPI.sendMap(players[i], mapId, info.getData(), this.plugin.getConfigManager().getProperties().mapPacketInterval);
        }
    }

    private static String createHealthBar(double health, double maxHealth) {
        String manaBar = com.herocraftonline.dev.heroes.ui.MapColor.DARK_RED + "[" + com.herocraftonline.dev.heroes.ui.MapColor.DARK_GREEN;
        int bars = 40;
        int progress = (int) (health / maxHealth * bars);
        for (int i = 0; i < progress; i++) {
            manaBar += "|";
        }
        manaBar += com.herocraftonline.dev.heroes.ui.MapColor.DARK_GRAY;
        for (int i = 0; i < bars - progress; i++) {
            manaBar += "|";
        }
        manaBar += com.herocraftonline.dev.heroes.ui.MapColor.RED + "]";
        double percent = (health / maxHealth * 100);
        DecimalFormat df = new DecimalFormat("#.##");
        return manaBar + " - " + com.herocraftonline.dev.heroes.ui.MapColor.GREEN + df.format(percent) + "%";
    }
}