package com.herocraftonline.dev.heroes.persistence;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.Packet18ArmAnimation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ExperienceGainEvent;
import com.herocraftonline.dev.heroes.api.LevelEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class Hero {

    private static final DecimalFormat decFormat = new DecimalFormat("#0.##");
    private static final Map<Material, Integer> durability;
    private static final Map<Material, Integer> armorPoints;
    static {
        Map<Material, Integer> aMap = new HashMap<Material, Integer>();
        aMap.put(Material.LEATHER_HELMET, 34);
        aMap.put(Material.LEATHER_CHESTPLATE, 49);
        aMap.put(Material.LEATHER_LEGGINGS, 46);
        aMap.put(Material.LEATHER_BOOTS, 40);

        aMap.put(Material.GOLD_HELMET, 68);
        aMap.put(Material.GOLD_CHESTPLATE, 96);
        aMap.put(Material.GOLD_LEGGINGS, 92);
        aMap.put(Material.GOLD_BOOTS, 80);

        aMap.put(Material.CHAINMAIL_HELMET, 67);
        aMap.put(Material.CHAINMAIL_CHESTPLATE, 96);
        aMap.put(Material.CHAINMAIL_LEGGINGS, 92);
        aMap.put(Material.CHAINMAIL_BOOTS, 79);

        aMap.put(Material.IRON_HELMET, 136);
        aMap.put(Material.IRON_CHESTPLATE, 192);
        aMap.put(Material.IRON_LEGGINGS, 184);
        aMap.put(Material.IRON_BOOTS, 160);

        aMap.put(Material.DIAMOND_HELMET, 272);
        aMap.put(Material.DIAMOND_CHESTPLATE, 384);
        aMap.put(Material.DIAMOND_LEGGINGS, 368);
        aMap.put(Material.DIAMOND_BOOTS, 320);
        durability = Collections.unmodifiableMap(aMap);

        Map<Material, Integer> bMap = new HashMap<Material, Integer>();
        bMap.put(Material.LEATHER_HELMET, 3);
        bMap.put(Material.LEATHER_CHESTPLATE, 8);
        bMap.put(Material.LEATHER_LEGGINGS, 6);
        bMap.put(Material.LEATHER_BOOTS, 3);

        bMap.put(Material.GOLD_HELMET, 3);
        bMap.put(Material.GOLD_CHESTPLATE, 8);
        bMap.put(Material.GOLD_LEGGINGS, 6);
        bMap.put(Material.GOLD_BOOTS, 3);

        bMap.put(Material.CHAINMAIL_HELMET, 3);
        bMap.put(Material.CHAINMAIL_CHESTPLATE, 8);
        bMap.put(Material.CHAINMAIL_LEGGINGS, 6);
        bMap.put(Material.CHAINMAIL_BOOTS, 3);

        bMap.put(Material.IRON_HELMET, 3);
        bMap.put(Material.IRON_CHESTPLATE, 8);
        bMap.put(Material.IRON_LEGGINGS, 6);
        bMap.put(Material.IRON_BOOTS, 3);

        bMap.put(Material.DIAMOND_HELMET, 3);
        bMap.put(Material.DIAMOND_CHESTPLATE, 8);
        bMap.put(Material.DIAMOND_LEGGINGS, 6);
        bMap.put(Material.DIAMOND_BOOTS, 3);
        armorPoints = Collections.unmodifiableMap(bMap);
    }

    protected final Heroes plugin;
    protected Player player;
    protected HeroClass heroClass;
    protected int mana = 0;
    protected HeroParty party = null;
    protected boolean verbose = true;
    protected Set<Effect> effects = new HashSet<Effect>();
    protected Map<String, Double> experience = new HashMap<String, Double>();
    protected Map<String, Long> cooldowns = new HashMap<String, Long>();
    protected Map<Entity, CreatureType> summons = new HashMap<Entity, CreatureType>();
    protected Map<Material, String[]> binds = new HashMap<Material, String[]>();
    protected List<ItemStack> itemRecovery = new ArrayList<ItemStack>();
    protected Set<String> suppressedSkills = new HashSet<String>();
    protected double health;

    public Hero(Heroes plugin, Player player, HeroClass heroClass) {
        this.plugin = plugin;
        this.player = player;
        this.heroClass = heroClass;

        int playerHealth = player.getHealth();
        this.health = playerHealth / 20.0 * getMaxHealth();;
    }

    public void syncHealth() {
        int playerHealth = (int) (health / getMaxHealth() * 20);
        getPlayer().setHealth(playerHealth);
    }

    public void addRecoveryItem(ItemStack item) {
        this.itemRecovery.add(item);
    }

    public void bind(Material material, String[] skillName) {
        binds.put(material, skillName);
    }

    public void changeHeroClass(HeroClass heroClass) {
        setHeroClass(heroClass);
        binds.clear();
    }

    public void damage(int damage) {
        getPlayer();
        System.out.println("Config Damage: " + damage);

        // gotta do something about no damage ticks
        
        PlayerInventory inventory = player.getInventory();
        ItemStack[] armorContents = inventory.getArmorContents();

        int missingDurability = 0;
        int maxDurability = 0;
        int baseArmorPoints = 0;

        for (ItemStack armor : armorContents) {
            Material armorType = armor.getType();
            if (armorType != Material.AIR) {
                short armorDurability = armor.getDurability();
                missingDurability += armorDurability;
                maxDurability += armorType.getMaxDurability();
                baseArmorPoints += armorPoints.get(armorType);
                armor.setDurability((short) (armorDurability - damage));
            }
        }
        
        inventory.setArmorContents(armorContents);
        player.updateInventory();

        System.out.println("    Missing Durability: " + missingDurability);
        System.out.println("    Max Durability: " + maxDurability);
        System.out.println("    Base Armor Points: " + baseArmorPoints);

        if (maxDurability == 0) {
            maxDurability = 1;
        }

        double armorPoints = (double) baseArmorPoints * (maxDurability + missingDurability) / maxDurability;
        System.out.println("    Armor Points: " + armorPoints);
        double damageReduction = 0.04 * armorPoints;
        System.out.println("    Damage Reduction: " + damageReduction);
        double reducedDamage = damage * (1 - damageReduction);
        System.out.println("    Reduced Damage: " + reducedDamage);
        System.out.println("    Initial Health: " + health);
        health -= reducedDamage;
        System.out.println("    Final Health: " + health);
        syncHealth();

        EntityLiving nmsEntity = ((CraftLivingEntity) player).getHandle();
        for (Player player : this.player.getWorld().getPlayers()) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().netServerHandler.sendPacket(new Packet18ArmAnimation(nmsEntity, (byte) 2));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Hero other = (Hero) obj;
        if (player == null) {
            if (other.player != null) {
                return false;
            }
        } else if (!player.getName().equals(other.player.getName())) {
            return false;
        }
        return true;
    }

    public void gainExp(double expGain, ExperienceType source) {
        gainExp(expGain, source, true);
    }

    public void gainExp(double expGain, ExperienceType source, boolean distributeToParty) {
        Properties prop = plugin.getConfigManager().getProperties();

        if (distributeToParty && party != null && party.getExp()) {
            Location location = getPlayer().getLocation();

            Set<Hero> partyMembers = party.getMembers();
            Set<Hero> inRangeMembers = new HashSet<Hero>();
            for (Hero partyMember : partyMembers) {
                if (location.distance(partyMember.getPlayer().getLocation()) <= 50) {
                    inRangeMembers.add(partyMember);
                }
            }

            int partySize = inRangeMembers.size();
            double sharedExpGain = expGain / partySize * ((partySize - 1) * prop.partyBonus + 1.0);

            for (Hero partyMember : inRangeMembers) {
                partyMember.gainExp(sharedExpGain, source, false);
            }

            return;
        }

        double exp = getExperience();

        // adjust exp using the class modifier
        expGain *= heroClass.getExpModifier();

        int currentLevel = prop.getLevel(exp);
        int newLevel = prop.getLevel(exp + expGain);
        if (currentLevel >= prop.maxLevel) {
            expGain = 0;
        }

        // add the experience
        exp += expGain;

        // call event
        ExperienceGainEvent expEvent;
        if (newLevel == currentLevel) {
            expEvent = new ExperienceGainEvent(this, expGain, source);
        } else {
            expEvent = new LevelEvent(this, expGain, currentLevel, newLevel, source);
        }
        plugin.getServer().getPluginManager().callEvent(expEvent);
        if (expEvent.isCancelled()) {
            // undo the experience gain
            exp -= expGain;
            return;
        }

        // undo the previous gain to make sure we use the updated value
        exp -= expGain;
        expGain = expEvent.getExpGain();

        // add the updated experience
        exp += expGain;

        // notify the user
        if (expGain != 0) {
            if (verbose) {
                Messaging.send(player, "$1: Gained $2 Exp", heroClass.getName(), decFormat.format(expGain));
            }
            if (newLevel != currentLevel) {
                player.setHealth(20);
                setHealth(getMaxHealth());
                Messaging.send(player, "You leveled up! (Lvl $1 $2)", String.valueOf(newLevel), heroClass.getName());
                if (newLevel >= prop.maxLevel) {
                    exp = prop.getExperience(prop.maxLevel);
                    Messaging.broadcast(plugin, "$1 has become a master $2!", player.getName(), heroClass.getName());
                    plugin.getHeroManager().saveHero(player);
                }
            }
        }

        setExperience(exp);
    }

    public Map<Material, String[]> getBinds() {
        return binds;
    }

    public Map<String, Long> getCooldowns() {
        return cooldowns;
    }

    public Set<Effect> getEffects() {
        return new HashSet<Effect>(effects);
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
        effect.apply(this);
    }

    public void removeEffect(Effect effect) {
        effects.remove(effect);
        effect.remove(this);
    }

    public boolean hasEffect(String name) {
        for (Effect effect : effects) {
            if (effect.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Effect getEffect(String name) {
        for (Effect effect : effects) {
            if (effect.getName().equalsIgnoreCase(name)) {
                return effect;
            }
        }
        return null;
    }

    public double getExperience() {
        return getExperience(heroClass);
    }

    public double getExperience(HeroClass heroClass) {
        Double exp = experience.get(heroClass.getName());
        return exp == null ? 0 : exp;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        int level = plugin.getConfigManager().getProperties().getLevel(getExperience());
        return heroClass.getBaseMaxHealth() + (level - 1) * heroClass.getMaxHealthPerLevel();
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public int getLevel() {
        return plugin.getConfigManager().getProperties().getLevel(getExperience());
    }

    public int getMana() {
        return mana;
    }

    public HeroParty getParty() {
        return party;
    }

    public Player getPlayer() {
        Player servPlayer = plugin.getServer().getPlayer(player.getName());
        if (servPlayer != null && player != servPlayer) {
            player = servPlayer;
        }
        return player;
    }

    public List<ItemStack> getRecoveryItems() {
        return this.itemRecovery;
    }

    public Map<Entity, CreatureType> getSummons() {
        return summons;
    }

    public Set<String> getSuppressedSkills() {
        return new HashSet<String>(suppressedSkills);
    }

    @Override
    public int hashCode() {
        return player == null ? 0 : player.getName().hashCode();
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean isMaster() {
        return isMaster(heroClass);
    }

    public boolean isMaster(HeroClass heroClass) {
        int maxExp = plugin.getConfigManager().getProperties().maxExp;
        if (getExperience(heroClass) >= maxExp || getExperience(heroClass) - maxExp > 0) {
            return true;
        }
        return false;
    }

    public boolean isSuppressing(Skill skill) {
        return suppressedSkills.contains(skill.getName());
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setExperience(double experience) {
        setExperience(heroClass, experience);
    }

    public void setExperience(HeroClass heroClass, double experience) {
        this.experience.put(heroClass.getName(), experience);
    }

    public void setHeroClass(HeroClass heroClass) {
        double currentMaxHP = getMaxHealth();
        this.heroClass = heroClass;
        double newMaxHP = getMaxHealth();
        health *= newMaxHP / currentMaxHP;
        if (health > newMaxHP) {
            health = newMaxHP;
        }

        // Check the Players inventory now that they have changed class.
        this.plugin.getInventoryChecker().checkInventory(getPlayer());
    }

    public void setMana(int mana) {
        if (mana > 100) {
            mana = 100;
        } else if (mana < 0) {
            mana = 0;
        }
        this.mana = mana;
    }

    public void setParty(HeroParty party) {
        this.party = party;
    }

    public void setRecoveryItems(List<ItemStack> items) {
        this.itemRecovery = items;
    }

    public void setSuppressed(Skill skill, boolean suppressed) {
        if (suppressed) {
            suppressedSkills.add(skill.getName());
        } else {
            suppressedSkills.remove(skill.getName());
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void unbind(Material material) {
        binds.remove(material);
    }

    public void setHealth(Double health) {
        double maxHealth = getMaxHealth();
        if (health > maxHealth) {
            this.health = maxHealth;
        } else if (health < 0) {
            this.health = 0;
        } else {
            this.health = health;
        }
    }

}
