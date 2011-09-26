package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillForage extends ActiveSkill{
 
    public SkillForage(Heroes plugin) {
        super(plugin, "Forage");
        setDescription("Forages for food.");
        setUsage("/skill forage");
        setArgumentRange(0, 0);
        setIdentifiers("skill forage");
        setTypes(SkillType.ITEM, SkillType.EARTH, SkillType.KNOWLEDGE);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("forest.items",  Arrays.asList(new String[] {"APPLE"}));
        node.setProperty("forest.chance", .01);
        node.setProperty("forest.max-found", 3);
        node.setProperty("plains.items",  Arrays.asList(new String[] {"WHEAT", "MELON"}));
        node.setProperty("plains.chance", .01);
        node.setProperty("plains.max-found", 3);
        node.setProperty("water.items",  Arrays.asList(new String[] {"RAW_FISH"}));
        node.setProperty("water.chance", .01);
        node.setProperty("water.max-found", 3);
        node.setProperty("swamp.items", Arrays.asList(new String[] {"RED_MUSHROOM", "BROWN_MUSHROOM", "RAW_FISH", "VINE"}));
        node.setProperty("swamp.chance", .01);
        node.setProperty("swamp.max-found", 4);
        node.setProperty("desert.items", Arrays.asList(new String[] {"CACTUS", "SUGAR_CANE"}));
        node.setProperty("desert.chance", .005);
        node.setProperty("desert.max-found", 2);
        node.setProperty("hell.items", Arrays.asList(new String[] {"ROTTEN_FLESH"}));
        node.setProperty("hell.chance", .005);
        node.setProperty("hell.max-found", 1);
        node.setProperty("sky.items", Arrays.asList(new String[] {"VINE"}));
        node.setProperty("sky.chance", .01);
        node.setProperty("sky.max-found", 3);
        node.setProperty("ice.items", new ArrayList<String>());
        node.setProperty("ice.chance", 0.0D);
        node.setProperty("ice.max-found", 0);
        node.setProperty("default.items", new ArrayList<String>());
        return node;
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Location loc = player.getLocation();
        HeroClass heroClass = hero.getHeroClass();
        Biome biome = player.getWorld().getBiome(loc.getBlockX(), loc.getBlockZ());

        double chance = 0;
        int maxFinds = 0;
        //Get the list of foragable stuff here
        List<String> materialNames = new ArrayList<String>();
        switch (biome) {
        default: 
            materialNames.addAll(getSetting(heroClass, "default.items", new ArrayList<String>()));
        case TAIGA :
        case TUNDRA :
        case ICE_DESERT :
            materialNames.addAll(getSetting(heroClass, "ice.items", new ArrayList<String>()));
            chance = getSetting(heroClass, "ice.chance", .01) * hero.getLevel();
            maxFinds = getSetting(heroClass, "ice.max-found", 3);
            break;
        case FOREST :
        case RAINFOREST :
        case SEASONAL_FOREST :
        case EXTREME_HILLS :
            materialNames.addAll(getSetting(heroClass, "forest.items", Arrays.asList(new String[] {"APPLE", "MELON"})));
            chance = getSetting(heroClass, "forest.chance", .01) * hero.getLevel();
            maxFinds = getSetting(heroClass, "forest.max-found", 3);
            break;
        case SWAMPLAND :
            materialNames.addAll(getSetting(heroClass, "swamp.items", Arrays.asList(new String[] {"RED_MUSHROOM", "BROWN_MUSHROOM", "RAW_FISH", "VINE"})));
            chance = getSetting(heroClass, "swamp.chance", .01) * hero.getLevel();
            maxFinds = getSetting(heroClass, "swamp.max-found", 4);
            break;
        case SAVANNA :
        case SHRUBLAND :
        case PLAINS :
            materialNames.addAll(getSetting(heroClass, "plains.items", Arrays.asList(new String[] {"WHEAT"})));
            chance = getSetting(heroClass, "plains.chance", .01) * hero.getLevel();
            maxFinds = getSetting(heroClass, "plains.max-found", 3);
            break;
        case DESERT :
            materialNames.addAll(getSetting(heroClass, "desert.items", Arrays.asList(new String[] {"CACTUS"})));
            chance = getSetting(heroClass, "desert.chance", .005) * hero.getLevel();
            maxFinds = getSetting(heroClass, "desert.max-found", 2);
            break;
        case OCEAN :
        case RIVER :
            materialNames.addAll(getSetting(heroClass, "water.items", Arrays.asList(new String[] {"RAW_FISH"})));
            chance = getSetting(heroClass, "water.chance", .01) * hero.getLevel();
            maxFinds = getSetting(heroClass, "water.max-found", 3);
            break;
        case HELL :
            materialNames.addAll(getSetting(heroClass, "hell.items", Arrays.asList(new String[] {"ROTTEN_FLESH"})));
            chance = getSetting(heroClass, "hell.chance", .005) * hero.getLevel();
            maxFinds = getSetting(heroClass, "hell.max-found", 1);
            break;
        case SKY :
            materialNames.addAll(getSetting(heroClass, "sky.items", Arrays.asList(new String[] {"VINE"})));
            chance = getSetting(heroClass, "sky.chance", .01) * hero.getLevel();
            maxFinds = getSetting(heroClass, "sky.max-found", 3);
            break;
        }
        
        List<Material> materials = new ArrayList<Material>();
        for (String name : materialNames) {
            try {
                materials.add(Material.valueOf(name));
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        
        if (materials.isEmpty() || Util.rand.nextDouble() >= chance || maxFinds <= 0) {
            Messaging.send(player, "You found nothing while foraging.");
            return false;
        } 
        
        int numItems = Util.rand.nextInt(maxFinds) + 1;
        for (int i = 0; i < numItems; i++) {
            ItemStack item = new ItemStack(materials.get(Util.rand.nextInt(materials.size())), 1);
            
            //If it doesn't fit in the players inventory add it as a recovery item
            Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(item);
            if (!leftOvers.isEmpty()) {
                for (ItemStack leftOver : leftOvers.values()) {
                    hero.addRecoveryItem(leftOver);
                }
            }
        }
        broadcastExecuteText(hero);
        return true; 
        
    }
}