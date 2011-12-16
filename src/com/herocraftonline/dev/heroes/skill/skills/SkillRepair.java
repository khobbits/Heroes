package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.api.SkillResult.ResultType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillRepair extends ActiveSkill {

    public SkillRepair(Heroes plugin) {
        super(plugin, "Repair");
        setDescription("repairs the tool or armor that you are holding");
        setUsage("/skill repair");
        setArgumentRange(0, 0);
        setIdentifiers("skill repair");
        setTypes(SkillType.ITEM, SkillType.PHYSICAL, SkillType.KNOWLEDGE);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("wood-weapons", 1);
        node.set("stone-weapons", 1);
        node.set("iron-weapons", 1);
        node.set("gold-weapons", 1);
        node.set("diamond-weapons", 1);
        node.set("leather-armor", 1);
        node.set("iron-armor", 1);
        node.set("gold-armor", 1);
        node.set("diamond-armor", 1);
        node.set("wood-tools", 1);
        node.set("stone-tools", 1);
        node.set("iron-tools", 1);
        node.set("gold-tools", 1);
        node.set("diamond-tools", 1);
        node.set("fishing-rod", 1);
        node.set("shears", 1);
        node.set("flint-steel", 1);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        ItemStack is = player.getItemInHand();
        Material reagent = null;
        int level = 0;

        switch (is.getType()) {
        case WOOD_SWORD:
        case WOOD_AXE:
            level = SkillConfigManager.getUseSetting(hero, this, "wood-weapons", 1, true);
        case WOOD_HOE:
        case WOOD_PICKAXE:
        case WOOD_SPADE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "wood-tools", 1, true);
            reagent = Material.WOOD;
            break;
        case STONE_SWORD:
        case STONE_AXE:
            level = SkillConfigManager.getUseSetting(hero, this, "stone-weapons", 1, true);
        case STONE_HOE:
        case STONE_PICKAXE:
        case STONE_SPADE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "stone-tools", 1, true);
            reagent = Material.COBBLESTONE;
            break;
        case SHEARS:
            level = SkillConfigManager.getUseSetting(hero, this, "shears", 1, true);
        case FLINT_AND_STEEL:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "flint-steel", 1, true);
        case IRON_CHESTPLATE:
        case IRON_LEGGINGS:
        case IRON_BOOTS:
        case IRON_HELMET:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "iron-armor", 1, true);
        case IRON_SWORD:
        case IRON_AXE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "iron-weapons", 1, true);
        case IRON_HOE:
        case IRON_PICKAXE:
        case IRON_SPADE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "iron-tools", 1, true);
            reagent = Material.IRON_INGOT;
            break;
        case GOLD_CHESTPLATE:
        case GOLD_LEGGINGS:
        case GOLD_BOOTS:
        case GOLD_HELMET:
            level = SkillConfigManager.getUseSetting(hero, this, "gold-armor", 1, true);
        case GOLD_SWORD:
        case GOLD_AXE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "gold-weapons", 1, true);
        case GOLD_HOE:
        case GOLD_PICKAXE:
        case GOLD_SPADE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "gold-tools", 1, true);
            reagent = Material.GOLD_INGOT;
            break;
        case DIAMOND_CHESTPLATE:
        case DIAMOND_LEGGINGS:
        case DIAMOND_BOOTS:
        case DIAMOND_HELMET:
            level = SkillConfigManager.getUseSetting(hero, this, "diamond-armor", 1, true);
        case DIAMOND_SWORD:
        case DIAMOND_AXE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "diamond-weapons", 1, true);
        case DIAMOND_HOE:
        case DIAMOND_PICKAXE:
        case DIAMOND_SPADE:
            if (level == 0)
                level = SkillConfigManager.getUseSetting(hero, this, "diamond-tools", 1, true);
            reagent = Material.DIAMOND;
            break;
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
            level = SkillConfigManager.getUseSetting(hero, this, "leather-armor", 1, true);
            reagent = Material.LEATHER;
            break;
        case FISHING_ROD:
            level = SkillConfigManager.getUseSetting(hero, this, "fishing-rod", 1, true);
            reagent = Material.STRING;
            break;
        default:
            Messaging.send(player, "You are not holding a repairable tool.");
            return SkillResult.FAIL;
        }
        
        if (hero.getSkillLevel(this) < level) {
            Messaging.send(player, "You must be level $1 to repair $2", level, is.getType().name().replace("_", " ").toLowerCase());
            return new SkillResult(ResultType.LOW_LEVEL, false);
        }
        ItemStack reagentStack = new ItemStack(reagent, getRepairCost(is));
        if (!hasReagentCost(player, reagentStack)) {
            return new SkillResult(ResultType.MISSING_REAGENT, true, reagentStack.getAmount(), reagentStack.getType().name().toLowerCase().replace("_", " "));
        }
        
        is.setDurability((short) 0);
        player.getInventory().removeItem(reagentStack);
        Util.syncInventory(player, plugin);
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    private int getRepairCost(ItemStack is) {
        Material mat = is.getType();
        int amt = 0;
        switch (mat) {
        case WOOD_AXE:
        case WOOD_PICKAXE:
        case STONE_AXE:
        case STONE_PICKAXE:
        case IRON_AXE:
        case IRON_PICKAXE:
        case GOLD_AXE:
        case GOLD_PICKAXE:
        case DIAMOND_AXE:
        case DIAMOND_PICKAXE:
            amt = (int) (is.getDurability() / (mat.getMaxDurability() / 3.0));
            return amt < 1 ? 1 : amt;
        case WOOD_SWORD:
        case WOOD_HOE:
        case STONE_SWORD:
        case STONE_HOE:
        case IRON_SWORD:
        case IRON_HOE:
        case GOLD_SWORD:
        case GOLD_HOE:
        case DIAMOND_SWORD:
        case DIAMOND_HOE:
        case SHEARS:
        case FISHING_ROD:
            amt = (int) (is.getDurability() / (mat.getMaxDurability() / 2.0));
            return amt < 1 ? 1 : amt;
        case LEATHER_BOOTS:
        case IRON_BOOTS:
        case GOLD_BOOTS:
        case DIAMOND_BOOTS:
            amt = (int) (is.getDurability() / (mat.getMaxDurability() / 4.0));
            return amt < 1 ? 1 : amt;
        case LEATHER_HELMET:
        case IRON_HELMET:
        case GOLD_HELMET:
        case DIAMOND_HELMET:
            amt = (int) (is.getDurability() / (mat.getMaxDurability() / 5.0));
            return amt < 1 ? 1 : amt;
        case LEATHER_CHESTPLATE:
        case IRON_CHESTPLATE:
        case GOLD_CHESTPLATE:
        case DIAMOND_CHESTPLATE:
            amt = (int) (is.getDurability() / (mat.getMaxDurability() / 8.0));
            return amt < 1 ? 1 : amt;
        case LEATHER_LEGGINGS:
        case IRON_LEGGINGS:
        case GOLD_LEGGINGS:
        case DIAMOND_LEGGINGS:
            amt = (int) (is.getDurability() / (mat.getMaxDurability() / 7.0));
            return amt < 1 ? 1 : amt;
        default:
            return 1;
        }
    }
}