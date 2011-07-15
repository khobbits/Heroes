package com.herocraftonline.dev.heroes.classes;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.util.config.ConfigurationNode;

public class HeroClass {

    private String name;

    private String description;

    private HeroClass parent;

    private Set<String> allowedArmor;

    private Set<String> allowedWeapons;

    private Set<ExperienceType> experienceSources;
    private double expModifier;
    private Map<String, ConfigurationNode> skills;
    private Set<HeroClass> specializations;
    private double maxHealth;

    public HeroClass() {
        name = new String();
        description = new String();
        allowedArmor = new LinkedHashSet<String>();
        allowedWeapons = new LinkedHashSet<String>();
        experienceSources = new LinkedHashSet<ExperienceType>();
        expModifier = 1.0D;
        specializations = new LinkedHashSet<HeroClass>();
        skills = new LinkedHashMap<String, ConfigurationNode>();
        maxHealth = 20;
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

    public double getMaxHealth() {
        return maxHealth;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExperienceSources(Set<ExperienceType> experienceSources) {
        this.experienceSources = experienceSources;
    }

    public void setExpModifier(double modifier) {
        this.expModifier = modifier;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
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
