package com.herocraftonline.dev.heroes.classes;

import com.herocraftonline.dev.heroes.damage.DamageManager.ProjectileType;
import org.bukkit.Material;
import org.bukkit.util.config.ConfigurationNode;

import java.util.*;

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
    private double expModifier;
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

    public void setItemDamage(Material material, int damage) {
        itemDamage.put(material, damage);
    }

    public void setProjectileDamage(ProjectileType type, int damage) {
        projectileDamage.put(type, damage);
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

    public double getMaxHealthPerLevel() {
        return maxHealthPerLevel;
    }

    public double getBaseMaxHealth() {
        return baseMaxHealth;
    }

    public String getName() {
        return name;
    }

    public HeroClass getParent() {
        return parent == null ? null : parent;
    }

    public ConfigurationNode getSkillSettings(String name) {
        return skills.get(name.toLowerCase());
    }

    public Integer getItemDamage(Material material) {
        return itemDamage.get(material);
    }

    public Integer getProjectileDamage(ProjectileType type) {
        return projectileDamage.get(type);
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

    public void removeSkill(String name) {
        skills.remove(name.toLowerCase());
    }

    public void removeDamageValue(Material material) {
        itemDamage.remove(material);
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

    public void setBaseMaxHealth(double baseMaxHealth) {
        this.baseMaxHealth = baseMaxHealth;
    }

    public void setMaxHealthPerLevel(double maxHealthPerLevel) {
        this.maxHealthPerLevel = maxHealthPerLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(HeroClass parent) {
        this.parent = parent;
    }

    public void setSpecializations(Set<HeroClass> specializations) {
        this.specializations = specializations;
    }

    @Override
    public String toString() {
        return name;
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
