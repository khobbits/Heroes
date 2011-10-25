package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillDeconstruct extends ActiveSkill {

    public SkillDeconstruct(Heroes plugin) {
        super(plugin, "Deconstruct");
        setDescription("Deconstructs the object you are holding.");
        setUsage("/skill deconstruct <list|info|item>");
        setArgumentRange(0, 2);
        setIdentifiers("skill deconstruct", "skill dstruct");
        setTypes(SkillType.ITEM, SkillType.KNOWLEDGE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        String root = "IRON_AXE";
        node.setProperty("require-workbench", true);
        node.setProperty(root + "." + Setting.LEVEL.node(), 1);
        node.setProperty(root + "." + Setting.EXP.node(), 0);
        node.setProperty(root + ".min-durability", .5); // Minimum durability percentage the item must have to
                                                        // deconstruct
        node.setProperty(root + ".IRON_INGOT", 1);
        node.setProperty(root + ".STICK", 1);
        node.setProperty(Setting.USE_TEXT.node(), "%hero% has deconstructed a %item%");
        return node;
    }

    @Override
    public void init() {
        super.init();
        setUseText(getSetting(null, Setting.USE_TEXT.node(), "%hero% has deconstructed a %item%").replace("%hero%", "$1").replace("%item%", "$2"));
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Set<String> items = new HashSet<String>(getSettingKeys(hero));
        items.remove("require-workbench");
        for (Setting set : Setting.values()) {
            items.remove(set.node());
        }
        int slot = -1;

        ItemStack item = null;
        if (args.length > 0) {
            if (args[0].toLowerCase().equals("list")) {
                Messaging.send(player, "You can deconstruct these items: " + items.toString().replace("[", "").replace("]", ""));
                return false;
            } else if (args[0].toLowerCase().equals("info")) {
                // Usage Checks if the player passed in arguments
                if (args.length < 2) {
                    Messaging.send(player, "Proper usage is /skill deconstruct info item");
                    return false;
                } else if (!items.contains(args[1])) {
                    Messaging.send(player, "You can't deconstruct that item!");
                    return false;
                } else {
                    // Iterate over the deconstruct recipe and get all the items/amounts it turns into
                    Messaging.send(player, args[1] + " deconstructs into the following items: ");
                    for (String s : getSettingKeys(hero, args[1])) {
                        if (s.equals("min-durability") || s.equals(Setting.LEVEL.node()) || s.equals(Setting.EXP.node())) {
                            continue;
                        }

                        int amount = getSetting(hero, args[1] + "." + s, 1, false);
                        Messaging.send(player, s.toLowerCase().replace("_", " ") + ": " + amount);
                    }

                    return false;
                }
            } else if (items.contains(args[0])) {
                item = new ItemStack(Material.matchMaterial(args[0]), 1);
                if (!player.getInventory().contains(item.getType(), 1)) {
                    Messaging.send(player, "You don't have any " + item.getType().name().toLowerCase().replace("_", " ") + " to deconstruct!");
                    return false;
                }
            }
            if (item == null) {
                Messaging.send(player, "Invalid item to deconstruct, or bad command!");
                return false;
            }
        } else {
            // if no args attempt to deconstruct item in hand
            item = player.getItemInHand().clone();
            item.setAmount(1);
            slot = player.getInventory().getHeldItemSlot();
        }

        if (getSetting(hero, "require-workbench", true) && player.getTargetBlock((HashSet<Byte>) null, 3).getType() != Material.WORKBENCH) {
            Messaging.send(player, "You must have a workbench targetted to deconstruct an item!");
            return false;
        }

        if (item.getType() == Material.AIR) {
            Messaging.send(player, "You must be holding the item you wish to deconstruct!");
            return false;
        }

        String matName = item.getType().name();
        if (!items.contains(matName)) {
            Messaging.send(player, "You can't deconstruct that item!");
            return false;
        }

        int level = getSetting(hero, matName + "." + Setting.LEVEL.node(), 1, true);
        if (level > hero.getLevel()) {
            Messaging.send(player, "You must be level " + level + " to deconstruct that item!");
            return false;
        }
        double minDurability = 0;
        if (item.getType().getMaxDurability() > 16) {
            minDurability = item.getType().getMaxDurability() * (1D - getSetting(hero, matName + ".min-durability", .5, true));
        }

        if (slot == -1) {
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i].getType() != item.getType()) {
                    continue;
                } else if (contents[i].getType().getMaxDurability() > 16 && contents[i].getDurability() > minDurability) {
                    continue;
                } else if (contents[i].getType().getMaxDurability() > 16 && slot != -1 && contents[i].getDurability() <= player.getInventory().getContents()[slot].getDurability()) {
                    continue;
                }
                slot = i;
            }
        }
        if (slot == -1) {
            Messaging.send(player, "That item does not have enough durability remaining!");
            return false;
        }

        List<String> returned = getSettingKeys(hero, matName);
        if (returned == null) {
            Messaging.send(player, "Unable to deconstruct that item!");
            return false;
        }

        for (String s : returned) {
            if (s.equals("min-durability") || s.equals(Setting.LEVEL.node()) || s.equals(Setting.EXP.node())) {
                continue;
            }

            Material m = Material.matchMaterial(s);
            if (m == null)
                throw new IllegalArgumentException("Error with skill " + getName() + ": bad item definition " + s);
            int amount = getSetting(hero, matName + "." + s, 1, false);
            if (amount < 1)
                throw new IllegalArgumentException("Error with skill " + getName() + ": bad amount definition for " + s + ": " + amount);

            ItemStack stack = new ItemStack(m, amount);
            Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(stack);
            // Drop any leftovers we couldn't add to the players inventory
            if (!leftOvers.isEmpty()) {
                for (ItemStack leftOver : leftOvers.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                }
                Messaging.send(player, "Items have been dropped at your feet!");
            }
        }
        int amount = player.getInventory().getContents()[slot].getAmount() - 1;
        if (amount == 0) {
            player.getInventory().clear(slot);
        } else {
            player.getInventory().getContents()[slot].setAmount(amount);
        }
        Util.syncInventory(player, plugin);

        // Grant the hero experience
        int xp = getSetting(hero, matName + "." + Setting.EXP.node(), 0, false);
        hero.gainExp(xp, ExperienceType.CRAFTING);

        broadcast(player.getLocation(), getUseText(), player.getDisplayName(), matName.toLowerCase().replace("_", " "));
        return true;
    }
}
