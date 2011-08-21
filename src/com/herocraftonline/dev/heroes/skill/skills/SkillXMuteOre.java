package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.util.MaterialUtil;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillXMuteOre extends ActiveSkill {

    public SkillXMuteOre(Heroes plugin) {
        super(plugin, "XMuteOre");
        setDescription("Transmutes ores into more valuable ones");
        setUsage("/skill xmuteore");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill xmuteore" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        Map<String, Object> coalMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = -1219378148267575L;

            {
                put("reagentdata", 0); // Corresponds to coal (not charcoal)
                put("product", "IRON_ORE");
                put("count", 5);
            }
        };
        Map<String, Object> ironMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = -7888166535021352165L;

            {
                put("product", "GOLD_ORE");
                put("count", 32);
            }
        };
        Map<String, Object> lapisMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 2039299575654763981L;

            {
                put("reagentdata", 4); // Corresponds to lapis
                put("product", "DIAMOND");
                put("count", 10);
            }
        };
        node.setProperty("COAL", coalMap);
        node.setProperty("IRON_ORE", ironMap);
        node.setProperty("INK_SACK", lapisMap); // Represents lapis
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player p = hero.getPlayer();
        ItemStack is = p.getItemInHand();
        if (is == null) {
            Messaging.send(p, "You do not have any item in your hand!");
            return false;
        }
        Material mat = is.getType();
        Material nextMat = null;
        int count = 1;
        byte data = -1;

        // Narrowing primitive conversion
        data = (byte) getSetting(hero.getHeroClass(), mat.toString() + ".reagentdata", -1);
        count = getSetting(hero.getHeroClass(), mat.toString() + ".count", 1);
        String productName = getSetting(hero.getHeroClass(), mat.toString() + ".product", (String) null);
        nextMat = Material.getMaterial(productName);

        if (nextMat != null && count > 0) {
            if (data != -1 && is.getData().getData() == data) { // Prevent charcoal/inksacks from being used

                int leftOver = is.getAmount() % count;
                int productCount = is.getAmount() / count; // Can these two operations be merged?

                if (productCount != 0) {
                    ItemStack product = new ItemStack(nextMat, productCount);

                    Map<Integer, ItemStack> leftover = p.getInventory().addItem(product);
                    if (!leftover.isEmpty()) {
                        p.sendMessage("Dropping unstorable products onto ground!");
                        World w = p.getWorld();
                        Location loc = p.getLocation();
                        for (ItemStack excess : leftover.values()) {
                            w.dropItemNaturally(loc, excess);
                        }
                    }
                }

                if (leftOver != 0) {
                    is.setAmount(leftOver);
                } else {
                    is = null;
                }
                p.setItemInHand(is);

                p.sendMessage("You turn the " + MaterialUtil.getFriendlyName(mat) + " into " + MaterialUtil.getFriendlyName(nextMat) + "!");
                broadcast(p.getLocation(), getUseText(), p.getName(), getName());
                return true;
            }
        }

        return false;
    }
}
