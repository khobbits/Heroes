package com.herocraftonline.dev.heroes.classes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.damage.DamageManager.ProjectileType;

public class HeroClass {

    private String name;
    private String description;
    private HeroClass parent;
    private Set<HeroClass> specializations;
    private Set<String> allowedArmor;
    private Set<String> allowedWeapons;
    private Set<ExperienceType> experienceSources;
    private Map<Material, Integer> itemDamage;
    private Map<ProjectileType, Integer> projectileDamage;
    private Map<String, ConfigurationNode> skills;
    // private Map<String, SkillData> skillData;
    private int maxLevel;
    private int cost;
    private double expModifier;
    private double expLoss;
    private double baseMaxHealth;
    private double maxHealthPerLevel;

    public HeroClass() {
        name = "";
        description = "";
        allowedArmor = new LinkedHashSet<String>();
        allowedWeapons = new LinkedHashSet<String>();
        itemDamage = new HashMap<Material, Integer>();
        projectileDamage = new HashMap<ProjectileType, Integer>();
        experienceSources = new LinkedHashSet<ExperienceType>();
        expModifier = 1.0D;
        specializations = new LinkedHashSet<HeroClass>();
        skills = new LinkedHashMap<String, ConfigurationNode>();
        baseMaxHealth = 20;
        maxHealthPerLevel = 0;
        maxLevel = 1;
        cost = 0;
    }

    public HeroClass(String name) {
        this();
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

    public Set<String> getSkillNames() {
        return new TreeSet<String>(skills.keySet());
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
        HeroClass other = (HeroClass) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public Set<String> getAllowedArmor() {
        return this.allowedArmor;
    }

    public Set<String> getAllowedWeapons() {
        return this.allowedWeapons;
    }

    public String getDescription() {
        return description;
    }

    public Set<ExperienceType> getExperienceSources() {
        return experienceSources;
    }

    public double getExpModifier() {
        return this.expModifier;
    }

    public Integer getItemDamage(Material material) {
        return itemDamage.get(material);
    }

    public String getName() {
        return name;
    }

    public double getMaxHealthPerLevel() {
        return maxHealthPerLevel;
    }

    public double getBaseMaxHealth() {
        return baseMaxHealth;
    }

    public HeroClass getParent() {
        return parent == null ? null : parent;
    }

    public Integer getProjectileDamage(ProjectileType type) {
        return projectileDamage.get(type);
    }

    public ConfigurationNode getSkillSettings(String name) {
        return skills.get(name.toLowerCase());
    }

    public Set<HeroClass> getSpecializations() {
        return specializations;
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public boolean hasSkill(String name) {
        return skills.containsKey(name.toLowerCase());
    }
    
    public boolean isPrimary() {
        return parent == null;
    }

    public void removeDamageValue(Material material) {
        itemDamage.remove(material);
    }

    public void removeSkill(String name) {
        skills.remove(name.toLowerCase());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExperienceSources(Set<ExperienceType> experienceSources) {
        this.experienceSources = experienceSources;
    }

    public void setExpModifier(double modifier) {
        this.expModifier = modifier;
    }

    public void setItemDamage(Material material, int damage) {
        itemDamage.put(material, damage);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseMaxHealth(double baseMaxHealth) {
        this.baseMaxHealth = baseMaxHealth;
    }

    public void setMaxHealthPerLevel(double maxHealthPerLevel) {
        this.maxHealthPerLevel = maxHealthPerLevel;
    }

    public void setParent(HeroClass parent) {
        this.parent = parent;
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

    /**
     * @param expLoss the expLoss to set
     */
    public void setExpLoss(double expLoss) {
        this.expLoss = expLoss;
    }

    /**
     * @return the expLoss
     */
    public double getExpLoss() {
        return expLoss;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
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

}
