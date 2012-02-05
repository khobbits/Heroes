package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.api.SkillResult.ResultType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.MaterialUtil;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkillRepair extends ActiveSkill {
    public SkillRepair(Heroes plugin) {
        super(plugin, "Repair");
        setDescription("You are able to repair tools or armor that you are holding.");
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
        node.set("chain-armor", 1);
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
        node.set("unchant-chance", .5);
        node.set("unchant-chance-reduce", .005);
        return node;
    }

    private int getRepairCost(ItemStack is) {
        Material mat = is.getType();
        int amt;
        switch (mat) {
        case BOW:
            amt = (int) ((is.getDurability() / (double) mat.getMaxDurability()) * 2.0);
            return amt < 1 ? 1 : amt;
        case LEATHER_BOOTS:
        case IRON_BOOTS:
        case CHAINMAIL_BOOTS:
        case GOLD_BOOTS:
        case DIAMOND_BOOTS:
            amt = (int) ((is.getDurability() / (double) mat.getMaxDurability()) * 3.0);
            return amt < 1 ? 1 : amt;
        case LEATHER_HELMET:
        case IRON_HELMET:
        case CHAINMAIL_HELMET:
        case GOLD_HELMET:
        case DIAMOND_HELMET:
            amt = (int) ((is.getDurability() / (double) mat.getMaxDurability()) * 4.0);
            return amt < 1 ? 1 : amt;
        case LEATHER_CHESTPLATE:
        case IRON_CHESTPLATE:
        case CHAINMAIL_CHESTPLATE:
        case GOLD_CHESTPLATE:
        case DIAMOND_CHESTPLATE:
            amt = (int) ((is.getDurability() / (double) mat.getMaxDurability()) * 7.0);
            return amt < 1 ? 1 : amt;
        case LEATHER_LEGGINGS:
        case IRON_LEGGINGS:
        case CHAINMAIL_LEGGINGS:
        case GOLD_LEGGINGS:
        case DIAMOND_LEGGINGS:
            amt = (int) ((is.getDurability() / (double) mat.getMaxDurability()) * 6.0);
            return amt < 1 ? 1 : amt;
        default:
            return 1;
        }
    }

    private int getRequiredLevel(Hero hero, Material material) {
        switch (material) {
        case WOOD_SWORD:
        case WOOD_AXE:
        case BOW:
            return SkillConfigManager.getUseSetting(hero, this, "wood-weapons", 1, true);
        case WOOD_HOE:
        case WOOD_PICKAXE:
        case WOOD_SPADE:
            return SkillConfigManager.getUseSetting(hero, this, "wood-tools", 1, true);
        case STONE_SWORD:
        case STONE_AXE:
            return SkillConfigManager.getUseSetting(hero, this, "stone-weapons", 1, true);
        case STONE_HOE:
        case STONE_PICKAXE:
        case STONE_SPADE:
            return SkillConfigManager.getUseSetting(hero, this, "stone-tools", 1, true);
        case SHEARS:
            return SkillConfigManager.getUseSetting(hero, this, "shears", 1, true);
        case FLINT_AND_STEEL:
            return SkillConfigManager.getUseSetting(hero, this, "flint-steel", 1, true);
        case IRON_CHESTPLATE:
        case IRON_LEGGINGS:
        case IRON_BOOTS:
        case IRON_HELMET:
            return SkillConfigManager.getUseSetting(hero, this, "iron-armor", 1, true);
        case IRON_SWORD:
        case IRON_AXE:
            return SkillConfigManager.getUseSetting(hero, this, "iron-weapons", 1, true);
        case IRON_HOE:
        case IRON_PICKAXE:
        case IRON_SPADE:
            return SkillConfigManager.getUseSetting(hero, this, "iron-tools", 1, true);
        case CHAINMAIL_HELMET:
        case CHAINMAIL_CHESTPLATE:
        case CHAINMAIL_BOOTS:
        case CHAINMAIL_LEGGINGS:
            return SkillConfigManager.getUseSetting(hero, this, "chain-armor", 1, true);
        case GOLD_CHESTPLATE:
        case GOLD_LEGGINGS:
        case GOLD_BOOTS:
        case GOLD_HELMET:
            return SkillConfigManager.getUseSetting(hero, this, "gold-armor", 1, true);
        case GOLD_SWORD:
        case GOLD_AXE:
            return SkillConfigManager.getUseSetting(hero, this, "gold-weapons", 1, true);
        case GOLD_HOE:
        case GOLD_PICKAXE:
        case GOLD_SPADE:
            return SkillConfigManager.getUseSetting(hero, this, "gold-tools", 1, true);
        case DIAMOND_CHESTPLATE:
        case DIAMOND_LEGGINGS:
        case DIAMOND_BOOTS:
        case DIAMOND_HELMET:
            return SkillConfigManager.getUseSetting(hero, this, "diamond-armor", 1, true);
        case DIAMOND_SWORD:
        case DIAMOND_AXE:
            return SkillConfigManager.getUseSetting(hero, this, "diamond-weapons", 1, true);
        case DIAMOND_HOE:
        case DIAMOND_PICKAXE:
        case DIAMOND_SPADE:
            return SkillConfigManager.getUseSetting(hero, this, "diamond-tools", 1, true);
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
            return SkillConfigManager.getUseSetting(hero, this, "leather-armor", 1, true);
        case FISHING_ROD:
            return SkillConfigManager.getUseSetting(hero, this, "fishing-rod", 1, true);
        default:
            return -1;
        }
    }

    private Material getRequiredReagent(Material material) {
        switch (material) {
        case WOOD_SWORD:
        case WOOD_AXE:
        case WOOD_HOE:
        case WOOD_PICKAXE:
        case WOOD_SPADE:
            return Material.WOOD;
        case STONE_SWORD:
        case STONE_AXE:
        case STONE_HOE:
        case STONE_PICKAXE:
        case STONE_SPADE:
            return Material.COBBLESTONE;
        case SHEARS:
        case FLINT_AND_STEEL:
        case IRON_CHESTPLATE:
        case IRON_LEGGINGS:
        case IRON_BOOTS:
        case IRON_HELMET:
        case IRON_SWORD:
        case IRON_AXE:
        case IRON_HOE:
        case IRON_PICKAXE:
        case IRON_SPADE:
            return Material.IRON_INGOT;
        case GOLD_CHESTPLATE:
        case GOLD_LEGGINGS:
        case GOLD_BOOTS:
        case GOLD_HELMET:
        case GOLD_SWORD:
        case GOLD_AXE:
        case GOLD_HOE:
        case GOLD_PICKAXE:
        case GOLD_SPADE:
            return Material.GOLD_INGOT;
        case DIAMOND_CHESTPLATE:
        case DIAMOND_LEGGINGS:
        case DIAMOND_BOOTS:
        case DIAMOND_HELMET:
        case DIAMOND_SWORD:
        case DIAMOND_AXE:
        case DIAMOND_HOE:
        case DIAMOND_PICKAXE:
        case DIAMOND_SPADE:
            return Material.DIAMOND;
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
            return Material.LEATHER;
        case FISHING_ROD:
        case BOW:
            return Material.STRING;
        case CHAINMAIL_HELMET:
        case CHAINMAIL_CHESTPLATE:
        case CHAINMAIL_BOOTS:
        case CHAINMAIL_LEGGINGS:
            return Material.IRON_FENCE;
        default:
            return null;
        }
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        ItemStack is = player.getItemInHand();
        Material isType = is.getType();
        int level = getRequiredLevel(hero, isType);
        Material reagent = getRequiredReagent(isType);

        if (level == -1 || reagent == null) {
            Messaging.send(player, "You are not holding a repairable tool.");
            return SkillResult.FAIL;
        }

        if (hero.getSkillLevel(this) < level) {
            Messaging.send(player, "You must be level $1 to repair $2", level, MaterialUtil.getFriendlyName(isType));
            return new SkillResult(ResultType.LOW_LEVEL, false);
        }
        ItemStack reagentStack = new ItemStack(reagent, getRepairCost(is));
        if (!hasReagentCost(player, reagentStack)) {
            return new SkillResult(ResultType.MISSING_REAGENT, true, reagentStack.getAmount(), MaterialUtil.getFriendlyName(reagentStack.getType()));
        }
        if(!is.getEnchantments().isEmpty()) {
            double unchant = SkillConfigManager.getUseSetting(hero, this, "unchant-chance", .5, true);
            unchant -= SkillConfigManager.getUseSetting(hero, this, "unchant-chance-reduce", .005, false) * hero.getSkillLevel(this);
            if (Util.rand.nextDouble() <= unchant) {
                for (Enchantment enchant : new ArrayList<Enchantment>(is.getEnchantments().keySet())) {
                    is.removeEnchantment(enchant);
                }
                Messaging.send(player, "Your item has lost it's power!");
            }
        }
        is.setDurability((short) 0);
        player.getInventory().removeItem(reagentStack);
        Util.syncInventory(player, plugin);
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}