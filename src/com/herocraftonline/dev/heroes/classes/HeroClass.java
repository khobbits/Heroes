package com.herocraftonline.dev.heroes.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.damage.DamageManager.ProjectileType;

public class HeroClass {

    private String name;
    private String description;
    private Set<HeroClass> strongParents = new HashSet<HeroClass>();
    private Set<HeroClass> weakParents = new HashSet<HeroClass>();
    private Set<HeroClass> specializations = new LinkedHashSet<HeroClass>();
    private Set<String> allowedArmor = new LinkedHashSet<String>();
    private Set<String> allowedWeapons = new LinkedHashSet<String>();
    private Set<ExperienceType> experienceSources = null;
    private boolean primary = true;
    private boolean secondary = true;
    private int tier = 0;
    private Map<Material, Integer> itemDamage = new EnumMap<Material, Integer>(Material.class);
    private Map<ProjectileType, Integer> projectileDamage = new EnumMap<ProjectileType, Integer>(ProjectileType.class);
    private Map<String, ConfigurationNode> skills = new LinkedHashMap<String, ConfigurationNode>();

    // private Map<String, SkillData> skillData;
    private int maxLevel;
    private int cost;
    private double expModifier;
    private double expLoss;
    private double baseMaxHealth;
    private double maxHealthPerLevel;
    private boolean userClass = true;
    private final Heroes plugin;

    public HeroClass(Heroes plugin) {
        this.plugin = plugin;
        name = "";
        description = "";
        expModifier = 1.0D;
        baseMaxHealth = 20;
        maxHealthPerLevel = 0;
        maxLevel = 1;
        cost = 0;
    }

    public HeroClass(String name, Heroes plugin) {
        this(plugin);
        this.name = name;
    }

    public void addAllowedArmor(String armor) {
        this.allowedArmor.add(armor);
    }

    public void addAllowedWeapon(String weapon) {
        this.allowedWeapons.add(weapon);
    }

    public void addSkill(String name, ConfigurationNode settings) {
        skills.put(name.toLowerCase(), settings);
    }

    public void addSpecialization(HeroClass heroClass) {
        specializations.add(heroClass);
    }

    public void addStrongParent(HeroClass parent) throws CircularParentException {
       if (parent.getAllParents().contains(this))
           throw new CircularParentException();
       
        strongParents.add(parent);
    }

    public void addWeakParent(HeroClass parent) throws CircularParentException {
        if (parent.getAllParents().contains(this))
            throw new CircularParentException();
        
        weakParents.add(parent);
    }
    
    public Set<HeroClass> getAllParents() {
        Set<HeroClass> classes = new HashSet<HeroClass>();
        for (HeroClass hClass : this.getParents()) {
            classes.addAll(hClass.getAllParents(new HashSet<HeroClass>(classes)));
        }
        return classes;
    }
    
    public Set<HeroClass> getAllParents(Set<HeroClass> parents) {
        for (HeroClass hClass : this.getParents()) {
            parents.addAll(hClass.getAllParents(new HashSet<HeroClass>(parents)));
        }
        return parents;
    }
    
    /**
     * @return if this class is the default class
     */
    public boolean isDefault() {
        return plugin.getClassManager().getDefaultClass().equals(this);
    }
    
    /**
     * @return the primary
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Allows this class to be selected as a primary class
     * @param primary the primary to set
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    /**
     * Allows this class to be selected as a secondary class
     * @return the secondary
     */
    public boolean isSecondary() {
        return secondary;
    }

    /**
     * @param secondary the secondary to set
     */
    public void setSecondary(boolean secondary) {
        this.secondary = secondary;
    }

    /**
     * Gets what tier this class is
     * @return the tier
     */
    public int getTier() {
        return tier;
    }

    /**
     * @param tier the tier to set
     */
    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HeroClass other = (HeroClass) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public Set<String> getAllowedArmor() {
        return this.allowedArmor;
    }

    public Set<String> getAllowedWeapons() {
        return this.allowedWeapons;
    }

    public double getBaseMaxHealth() {
        return baseMaxHealth;
    }

    public int getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public Set<ExperienceType> getExperienceSources() {
        return Collections.unmodifiableSet(experienceSources);
    }

    public boolean hasExperiencetype(ExperienceType type) {
        return experienceSources.contains(type);
    }
    /**
     * @return the expLoss
     */
    public double getExpLoss() {
        return expLoss;
    }

    public double getExpModifier() {
        return this.expModifier;
    }

    public Integer getItemDamage(Material material) {
        return itemDamage.get(material);
    }

    public double getMaxHealthPerLevel() {
        return maxHealthPerLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getName() {
        return name;
    }

    public List<HeroClass> getParents() {
        List<HeroClass> parents = new ArrayList<HeroClass>(strongParents);
        parents.addAll(weakParents);
        return Collections.unmodifiableList(parents);
    }

    public Integer getProjectileDamage(ProjectileType type) {
        return projectileDamage.get(type);
    }

    public Set<String> getSkillNames() {
        return new TreeSet<String>(skills.keySet());
    }

    public ConfigurationNode getSkillSettings(String name) {
        return skills.get(name.toLowerCase());
    }

    public Set<HeroClass> getSpecializations() {
        return Collections.unmodifiableSet(specializations);
    }

    public Set<HeroClass> getStrongParents() {
        return Collections.unmodifiableSet(strongParents);
    }

    public Set<HeroClass> getWeakParents() {
        return Collections.unmodifiableSet(weakParents);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public boolean hasSkill(String name) {
        return skills.containsKey(name.toLowerCase());
    }

    public boolean hasNoParents() {
        return strongParents.isEmpty() && weakParents.isEmpty();
    }

    public void removeDamageValue(Material material) {
        itemDamage.remove(material);
    }

    public void removeSkill(String name) {
        skills.remove(name.toLowerCase());
    }

    public void setBaseMaxHealth(double baseMaxHealth) {
        this.baseMaxHealth = baseMaxHealth;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExperienceSources(Set<ExperienceType> experienceSources) {
        this.experienceSources = experienceSources;
    }

    /**
     * @param expLoss
     *            the expLoss to set
     */
    public void setExpLoss(double expLoss) {
        this.expLoss = expLoss;
    }

    public void setExpModifier(double modifier) {
        this.expModifier = modifier;
    }

    public void setItemDamage(Material material, int damage) {
        itemDamage.put(material, damage);
    }

    public void setMaxHealthPerLevel(double maxHealthPerLevel) {
        this.maxHealthPerLevel = maxHealthPerLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProjectileDamage(ProjectileType type, int damage) {
        projectileDamage.put(type, damage);
    }

    public void setSpecializations(Set<HeroClass> specializations) {
        this.specializations = specializations;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isUserClass() {
        return userClass;
    }

    public void setUserClass(boolean userClass) {
        this.userClass = userClass;
    }

    public static enum ArmorItems {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    public static enum ArmorType {
        LEATHER,
        IRON,
        GOLD,
        DIAMOND,
        CHAINMAIL
    }

    public static enum ExperienceType {
        SKILL,
        KILLING,
        PVP,
        MINING,
        CRAFTING,
        LOGGING,
        DEATH,
        ADMIN,
        EXTERNAL
    }

    public static enum WeaponItems {
        PICKAXE,
        AXE,
        HOE,
        SPADE,
        SWORD
    }

    public static enum WeaponType {
        WOOD,
        STONE,
        IRON,
        GOLD,
        DIAMOND
    }

    @SuppressWarnings("serial")
    public class CircularParentException extends Exception {
    }
}
