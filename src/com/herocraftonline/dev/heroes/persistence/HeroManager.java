package com.herocraftonline.dev.heroes.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.Expirable;
import com.herocraftonline.dev.heroes.effects.Periodic;
import com.herocraftonline.dev.heroes.skill.OutsourcedSkill;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

/**
 * Player management
 * 
 * @author Herocraft's Plugin Team
 */
public class HeroManager {

    private Heroes plugin;
    private Set<Hero> heroes;
    private File playerFolder;
    private final static int effectInterval = 2;
    private final static int manaInterval = 5;

    public HeroManager(Heroes plugin) {
        this.plugin = plugin;
        this.heroes = new HashSet<Hero>();
        playerFolder = new File(plugin.getDataFolder(), "players"); // Setup our Player Data Folder
        playerFolder.mkdirs(); // Create the folder if it doesn't exist.

        Runnable effectTimer = new EffectUpdater(this);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, effectTimer, 0, effectInterval);

        Runnable manaTimer = new ManaUpdater(this);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, manaTimer, 0, manaInterval);
    }

    public boolean addHero(Hero hero) {
        return heroes.add(hero);
    }

    public boolean containsPlayer(Player player) {
        return getHero(player) != null;
    }

    public Hero createNewHero(Player player) {
        Hero hero = new Hero(plugin, player, plugin.getClassManager().getDefaultClass());
        hero.setMana(100);
        hero.setHealth(hero.getMaxHealth());
        hero.syncHealth();
        addHero(hero);
        return hero;
    }

    public Hero getHero(Player player) {
        for (Hero hero : getHeroes()) {
            if (hero == null || hero.getPlayer() == null) {
                removeHero(hero); // Seeing as it's null we might as well remove it.
                continue;
            }
            if (player.getName().equalsIgnoreCase(hero.getPlayer().getName()))
                return hero;
        }
        // If it gets to this stage then clearly the HeroManager doesn't have it so we create it...
        return loadHero(player);
    }

    public Set<Hero> getHeroes() {
        return new HashSet<Hero>(heroes);
    }

    /**
     * Load the given Players Data file.
     * 
     * @param player
     * @return
     */
    public Hero loadHero(Player player) {
        File playerFile = new File(playerFolder, player.getName() + ".yml"); // Setup our Players Data File.
        // Check if it already exists, if so we load the data.
        if (playerFile.exists()) {
            Configuration playerConfig = new Configuration(playerFile); // Setup the Configuration
            playerConfig.load(); // Load the Config File

            HeroClass playerClass = loadClass(player, playerConfig);
            Hero playerHero = new Hero(plugin, player, playerClass);

            loadCooldowns(playerHero, playerConfig);
            loadExperience(playerHero, playerConfig);
            loadRecoveryItems(playerHero, playerConfig);
            loadBinds(playerHero, playerConfig);
            playerHero.setMana(playerConfig.getInt("mana", 0));
            playerHero.setHealth(playerConfig.getDouble("health", playerClass.getBaseMaxHealth()));
            playerHero.setVerbose(playerConfig.getBoolean("verbose", true));
            playerHero.suppressedSkills = new HashSet<String>(playerConfig.getStringList("suppressed", null));

            addHero(playerHero);
            playerHero.syncHealth();

            performSkillChecks(playerHero);

            plugin.log(Level.INFO, "Loaded hero: " + player.getName());
            return playerHero;
        } else {
            // Create a New Hero with the Default Setup.
            plugin.log(Level.INFO, "Created hero: " + player.getName());
            return createNewHero(player);
        }
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
    public void saveHero(Player player) {
        File playerFile = new File(playerFolder, player.getName() + ".yml");
        Configuration playerConfig = new Configuration(playerFile);
        // Save the players stuff
        Hero hero = getHero(player);
        playerConfig.setProperty("class", hero.getHeroClass().toString());
        playerConfig.setProperty("verbose", hero.isVerbose());
        playerConfig.setProperty("suppressed", new ArrayList<String>(hero.getSuppressedSkills()));
        playerConfig.setProperty("mana", hero.getMana());
        playerConfig.removeProperty("itemrecovery");
        playerConfig.setProperty("health", hero.getHealth());

        saveCooldowns(hero, playerConfig);
        saveExperience(hero, playerConfig);
        saveRecoveryItems(hero, playerConfig);
        saveBinds(hero, playerConfig);

        playerConfig.save();
        plugin.log(Level.INFO, "Saved hero: " + player.getName());
    }

    public void stopTimers() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    private void loadBinds(Hero hero, Configuration config) {
        Map<Material, String[]> binds = new HashMap<Material, String[]>();
        List<String> bindKeys = config.getKeys("binds");
        if (bindKeys != null && bindKeys.size() > 0) {
            for (String material : bindKeys) {
                try {
                    Material item = Material.valueOf(material);
                    String bind = config.getString("binds." + material, "");
                    if (bind.length() > 0) {
                        binds.put(item, bind.split(" "));
                    }
                } catch (IllegalArgumentException e) {
                    this.plugin.debugLog(Level.WARNING, material + " isn't a valid Item to bind a Skill to.");
                    continue;
                }
            }
        }
        hero.binds = binds;
    }

    private HeroClass loadClass(Player player, Configuration config) {
        HeroClass playerClass = null;

        if (config.getString("class") != null) {
            playerClass = plugin.getClassManager().getClass(config.getString("class"));
            if (Heroes.Permissions != null && playerClass != plugin.getClassManager().getDefaultClass()) {
                if (!Heroes.Permissions.has(player, "heroes.classes." + playerClass.getName().toLowerCase())) {
                    playerClass = plugin.getClassManager().getDefaultClass();
                }
            }
        } else {
            playerClass = plugin.getClassManager().getDefaultClass();
        }
        return playerClass;
    }

    private void loadCooldowns(Hero hero, Configuration config) {
        HeroClass heroClass = hero.getHeroClass();

        String path = "cooldowns";
        List<String> storedCooldowns = config.getKeys(path);
        if (storedCooldowns != null) {
            long time = System.currentTimeMillis();
            Map<String, Long> cooldowns = new HashMap<String, Long>();
            for (String skillName : storedCooldowns) {
                long cooldown = (long) config.getDouble(path + "." + skillName, 0);
                if (heroClass.hasSkill(skillName) && cooldown > time) {
                    cooldowns.put(skillName, cooldown);
                }
            }
            hero.cooldowns = cooldowns;
        }
    }

    private void loadExperience(Hero hero, Configuration config) {
        if (hero == null || hero.getClass() == null || config == null)
            return;

        String root = "experience";
        List<String> expList = config.getKeys(root);
        if (expList != null) {
            for (String className : expList) {
                double exp = config.getDouble(root + "." + className, 0);
                HeroClass heroClass = plugin.getClassManager().getClass(className);
                if (heroClass != null) {
                    if (hero.getExperience(heroClass) == 0) {
                        hero.setExperience(heroClass, exp);
                        if (!heroClass.isPrimary() && exp > 0) {
                            hero.setExperience(heroClass.getParent(), plugin.getConfigManager().getProperties().maxExp);
                        }
                    }
                }
            }
        }
    }

    private void loadRecoveryItems(Hero hero, Configuration config) {
        List<ItemStack> itemRecovery = new ArrayList<ItemStack>();
        List<String> itemKeys = config.getKeys("itemrecovery");
        if (itemKeys != null && itemKeys.size() > 0) {
            for (String item : itemKeys) {
                try {
                    Short durability = Short.valueOf(config.getString("itemrecovery." + item, "0"));
                    Material type = Material.valueOf(item);
                    itemRecovery.add(new ItemStack(type, 1, durability));
                } catch (IllegalArgumentException e) {
                    this.plugin.debugLog(Level.WARNING, "Either '" + item + "' doesn't exist or the durability is of an incorrect value!");
                }
            }
        }
        hero.setRecoveryItems(itemRecovery);
    }

    private void performSkillChecks(Hero hero) {
        HeroClass playerClass = hero.getHeroClass();

        List<BaseCommand> commands = plugin.getCommandManager().getCommands();
        if (Heroes.Permissions != null) {
            for (BaseCommand cmd : commands) {
                if (cmd instanceof OutsourcedSkill) {
                    OutsourcedSkill skill = (OutsourcedSkill) cmd;
                    if (playerClass.hasSkill(skill.getName())) {
                        skill.tryLearningSkill(hero);
                    }
                }
            }
        }

        for (BaseCommand cmd : commands) {
            if (cmd instanceof PassiveSkill) {
                PassiveSkill skill = (PassiveSkill) cmd;
                if (playerClass.hasSkill(skill.getName())) {
                    skill.tryApplying(hero);
                }
            }
        }
    }

    private void saveBinds(Hero hero, Configuration config) {
        config.removeProperty("binds");
        Map<Material, String[]> binds = hero.getBinds();
        for (Material material : binds.keySet()) {
            String[] bindArgs = binds.get(material);
            StringBuilder bind = new StringBuilder();
            for (String arg : bindArgs) {
                bind.append(arg).append(" ");
            }
            config.setProperty("binds." + material.toString(), bind.toString().substring(0, bind.toString().length() - 1));
        }
    }

    private void saveCooldowns(Hero hero, Configuration config) {
        String path = "cooldowns";
        long time = System.currentTimeMillis();
        Map<String, Long> cooldowns = hero.getCooldowns();
        for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
            String skillName = entry.getKey();
            long cooldown = entry.getValue();
            if (cooldown > time) {
                System.out.println(path + "." + skillName);
                config.setProperty(path + "." + skillName, cooldown);
            }
        }
    }

    private void saveExperience(Hero hero, Configuration config) {
        if (hero == null || hero.getClass() == null || config == null)
            return;

        String root = "experience";
        for (Map.Entry<String, Double> entry : hero.experience.entrySet()) {
            config.setProperty(root + "." + entry.getKey(), (double) entry.getValue());
        }
    }

    private void saveRecoveryItems(Hero hero, Configuration config) {
        for (ItemStack item : hero.getRecoveryItems()) {
            String durability = Short.toString(item.getDurability());
            config.setProperty("itemrecovery." + item.getType().toString(), durability);
        }
    }
}

class EffectUpdater implements Runnable {

    private final HeroManager heroManager;

    EffectUpdater(HeroManager heroManager) {
        this.heroManager = heroManager;
    }

    @Override
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
    }
}

class ManaUpdater implements Runnable {

    private final HeroManager manager;
    private final long updateInterval = 5000;
    private long lastUpdate = 0;

    ManaUpdater(HeroManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        if (time < lastUpdate + updateInterval)
            return;
        lastUpdate = time;

        Set<Hero> heroes = manager.getHeroes();
        for (Hero hero : heroes) {
            if (hero == null) {
                continue;
            }

            int mana = hero.getMana();
            hero.setMana(mana > 100 ? mana : mana > 95 ? 100 : mana + 5); // Hooray for the ternary operator!
            if (mana != 100 && hero.isVerbose()) {
                Messaging.send(hero.getPlayer(), ChatColor.BLUE + "MANA " + Messaging.createManaBar(hero.getMana()));
            }
        }
    }
}
