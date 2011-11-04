package com.herocraftonline.dev.heroes.spout;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.util.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;

public class SpoutUI {

    public enum Cloaks{
        Mojang {
            public String toString(){
                return "http://www.minecraftwiki.net/images/b/be/Mojang.png";
            }
        },
        Million {
            public String toString(){
                return "http://www.minecraftwiki.net/images/f/f7/1MCape.png";
            }
        },
        dannyBstyle{
            public String toString(){
                return "http://www.minecraftwiki.net/images/4/40/DBCape.png";
            }
        },
        JulianClark{
            public String toString(){
                return "http://www.minecraftwiki.net/images/7/79/JulianClark.png";
            }
        },
        //MineCon{
        //    public String toString(){
        //        return "http://www.minecraftwiki.net/images/b/be/Mojang.png";
        //    }
        //},
        Christmas {
            public String toString(){
                return "http://www.minecraftwiki.net/images/3/33/Xmas.png";
            }
        },
        NewYears{
            public String toString(){
                return "http://www.minecraftwiki.net/images/b/b4/2011.png";
            }
        },
        Bacon{
            public String toString(){
                return "http://www.minecraftwiki.net/images/6/63/BaconCape.png";
            }
        }
    }


    public static void sendPlayerNotification(Player player, String title, String Body, Material material) {
        if (!Heroes.useSpout)
            return;
        if (SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
            SpoutManager.getPlayer(player).sendNotification(title, Body, material);
        }
    }

    public static void cloakPlayer(Player player, String cloakName){
        if(!Heroes.useSpout)
            return;
        if(SpoutManager.getPlayer(player).isSpoutCraftEnabled()){
            SpoutManager.getAppearanceManager().setGlobalCloak((HumanEntity) player, Cloaks.valueOf(cloakName).toString());
        }
    }

    /**
     * Get the colour of a LivingEntity target from a player's point of view.
     * 
     * used from mmoCore
     * @param player the player viewing the target
     * @param target the target to name
     * @return the colour to use
     */
    public static String getColor(Player player, LivingEntity target) {
        if (target instanceof Player) {
            if (((Player) target).isOp()) {
                return ChatColor.GOLD.toString();
            }
            return ChatColor.YELLOW.toString();
        } else {
            if (target instanceof Monster) {
                if (player != null && player.equals(((Monster) target).getTarget())) {
                    return ChatColor.RED.toString();
                } else {
                    return ChatColor.YELLOW.toString();
                }
            } else if (target instanceof WaterMob) {
                return ChatColor.GREEN.toString();
            } else if (target instanceof Flying) {
                return ChatColor.YELLOW.toString();
            } else if (target instanceof Animals) {
                if (player != null && player.equals(((Animals) target).getTarget())) {
                    return ChatColor.RED.toString();
                } else if (target instanceof Tameable) {
                    Tameable pet = (Tameable) target;
                    if (pet.isTamed()) {
                        return ChatColor.GREEN.toString();
                    } else {
                        return ChatColor.YELLOW.toString();
                    }
                } else {
                    return ChatColor.GRAY.toString();
                }
            } else {
                return ChatColor.GRAY.toString();
            }
        }
    }

    /**
     * Get a simple name for a living entity.
     * 
     * used from mmoCore with modifications
     * @param target the target we want named
     * @param showOwner if we prefix a pet's name with the owner's name
     * @return the full name
     */
    public static String getSimpleName(LivingEntity target, boolean showOwner) {
        String name = "";
        if (target instanceof Player) {
            name += ((Player) target).getDisplayName();
        } else if (target instanceof HumanEntity) {
            name += ((HumanEntity) target).getName();
        } else {
            if (target instanceof Tameable) {
                if (((Tameable) target).isTamed()) {
                    if (showOwner && ((Tameable) target).getOwner() instanceof Player) {
                        name += ((Player) ((Tameable) target).getOwner()).getName() + "'s ";
                    } else {
                        name += "Pet ";
                    }
                }
            }
            if (target instanceof Creature) {
                name += Messaging.getCreatureName((Creature) target);
            } else {
                name += "Unknown";
            }
        }
        return name;
    }
    
    /**
     * Get the percentage armour of a Player.
     * @param player the Player we're interested in
     * @return the percentage of max armour
     */
    public static int getArmor(Entity player) {
        if (player != null && player instanceof Player) {
            int armor = 0, max, multi[] = {15, 30, 40, 15};
            ItemStack inv[] = ((Player) player).getInventory().getArmorContents();
            for (int i = 0; i < inv.length; i++) {
                max = inv[i].getType().getMaxDurability();
                if (max >= 0) {
                    armor += multi[i] * (max - inv[i].getDurability()) / max;
                }
            }
            return armor;
        }
        return 0;
    }
}
