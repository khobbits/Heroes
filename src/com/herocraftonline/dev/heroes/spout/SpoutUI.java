package com.herocraftonline.dev.heroes.spout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
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
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.util.Messaging;

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

    @SuppressWarnings("deprecation")
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
            } else
                name += Messaging.getLivingEntityName(target);
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

    /**
     * Get the container for use by this plugin, anchor and position
     * @param player the player this is for
     * @param anchorName the name of the WidgetAnchor
     * @param offsetX the horizontal offset to use
     * @param offsetY the vertical offset to use
     * @return the Container
     */
    public static Container getContainer(SpoutPlayer player, String anchorName, int offsetX, int offsetY, Heroes plugin) {
        WidgetAnchor anchor = WidgetAnchor.SCALE;
        if ("TOP_LEFT".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.TOP_LEFT;
        } else if ("TOP_CENTER".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.TOP_CENTER;
            offsetX -= 213;
        } else if ("TOP_RIGHT".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.TOP_RIGHT;
            offsetX = -427 - offsetX;
        } else if ("CENTER_LEFT".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.CENTER_LEFT;
            offsetY -= 120;
        } else if ("CENTER_CENTER".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.CENTER_CENTER;
            offsetX -= 213;
            offsetY -= 120;
        } else if ("CENTER_RIGHT".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.CENTER_RIGHT;
            offsetX = -427 - offsetX;
            offsetY -= 120;
        } else if ("BOTTOM_LEFT".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.BOTTOM_LEFT;
            offsetY = -240 - offsetY;
        } else if ("BOTTOM_CENTER".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.BOTTOM_CENTER;
            offsetX -= 213;
            offsetY = -240 - offsetY;
        } else if ("BOTTOM_RIGHT".equalsIgnoreCase(anchorName)) {
            anchor = WidgetAnchor.BOTTOM_RIGHT;
            offsetX = -427 - offsetX;
            offsetY = -240 - offsetY;
        }
        Container container = (Container) new GenericContainer().setAlign(anchor).setAnchor(anchor).setFixed(true).setX(offsetX).setY(offsetY).setWidth(427).setHeight(240);
        player.getMainScreen().attachWidget(plugin, container);
        return container;
    }
}
