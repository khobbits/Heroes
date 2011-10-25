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
            materialNames.addAll(getSetting(hero, "default.items", new ArrayList<String>()));
        case TAIGA :
        case TUNDRA :
        case ICE_DESERT :
            materialNames.addAll(getSetting(hero, "ice.items", new ArrayList<String>()));
            chance = getSetting(hero, "ice.chance", .01, false) * hero.getLevel();
            maxFinds = getSetting(hero, "ice.max-found", 3, false);
            break;
        case FOREST :
        case RAINFOREST :
        case SEASONAL_FOREST :
        case EXTREME_HILLS :
            materialNames.addAll(getSetting(hero, "forest.items", Arrays.asList(new String[] {"APPLE", "MELON"})));
            chance = getSetting(hero, "forest.chance", .01, false) * hero.getLevel();
            maxFinds = getSetting(hero, "forest.max-found", 3, false);
            break;
        case SWAMPLAND :
            materialNames.addAll(getSetting(hero, "swamp.items", Arrays.asList(new String[] {"RED_MUSHROOM", "BROWN_MUSHROOM", "RAW_FISH", "VINE"})));
            chance = getSetting(hero, "swamp.chance", .01, false) * hero.getLevel();
            maxFinds = getSetting(hero, "swamp.max-found", 4, false);
            break;
        case SAVANNA :
        case SHRUBLAND :
        case PLAINS :
            materialNames.addAll(getSetting(hero, "plains.items", Arrays.asList(new String[] {"WHEAT"})));
            chance = getSetting(hero, "plains.chance", .01, false) * hero.getLevel();
            maxFinds = getSetting(hero, "plains.max-found", 3, false);
            break;
        case DESERT :
            materialNames.addAll(getSetting(hero, "desert.items", Arrays.asList(new String[] {"CACTUS"})));
            chance = getSetting(hero, "desert.chance", .005, false) * hero.getLevel();
            maxFinds = getSetting(hero, "desert.max-found", 2, false);
            break;
        case OCEAN :
        case RIVER :
            materialNames.addAll(getSetting(hero, "water.items", Arrays.asList(new String[] {"RAW_FISH"})));
            chance = getSetting(hero, "water.chance", .01, false) * hero.getLevel();
            maxFinds = getSetting(hero, "water.max-found", 3, false);
            break;
        case HELL :
            materialNames.addAll(getSetting(hero, "hell.items", Arrays.asList(new String[] {"ROTTEN_FLESH"})));
            chance = getSetting(hero, "hell.chance", .005, false) * hero.getLevel();
            maxFinds = getSetting(hero, "hell.max-found", 1, false);
            break;
        case SKY :
            materialNames.addAll(getSetting(hero, "sky.items", Arrays.asList(new String[] {"VINE"})));
            chance = getSetting(hero, "sky.chance", .01, false) * hero.getLevel();
            maxFinds = getSetting(hero, "sky.max-found", 3, false);
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
            
            Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(item);
            // Drop any leftovers we couldn't add to the players inventory
            if (!leftOvers.isEmpty()) {
                for (ItemStack leftOver : leftOvers.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                }
                Messaging.send(player, "Items have been dropped at your feet!");
            }
        }
        Util.syncInventory(player, plugin);
        broadcastExecuteText(hero);
        return true; 
        
    }
}