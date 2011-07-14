package com.herocraftonline.dev.heroes.skill;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

/**
 * A triggered skill that requires a target. TargettedSkills define a maximum distance setting. A target can be supplied
 * as the first argument to the command. If no such argument is provided, then the skill will use whatever target the
 * player is looking at within the configurable maximum distance, if any. The primary method to be overridden by
 * TargettedSkills is {@link #use(Hero, LivingEntity, String[])}, which is called by {@link #use(Hero, String[])} after
 * determining the target.
 * </br></br>
 * See {@link ActiveSkill} for an overview of command triggered skills.
 */
public abstract class TargettedSkill extends ActiveSkill {

    /**
     * Identifier used to store maximum targetting distance setting
     */
    public static final String SETTING_MAXDISTANCE = "max-distance";

    /**
     * When defining your own constructor, be sure to assign the name, description, usage, argument bounds and
     * identifier fields as defined in {@link com.herocraftonline.dev.heroes.command.BaseCommand}. Remember that each
     * identifier must begin with <i>skill</i>.
     * 
     * @param plugin
     *            the active Heroes instance
     */
    public TargettedSkill(Heroes plugin) {
        super(plugin);
    }

    /**
     * Loads and stores the skill's usage text from the configuration. By default, this text is
     * "%hero% used %skill% on %target!" where %hero%, %skill% and %target% are replaced with the Hero's, skill's and
     * target's names, respectively.
     */
    @Override
    public void init() {
        String useText = getSetting(null, SETTING_USETEXT, "%hero% used %skill% on %target%!");
        useText = useText.replace("%hero%", "$1").replace("%skill%", "$2").replace("%target%", "$3");
        setUseText(useText);
    }

    /**
     * Creates and returns a <code>ConfigurationNode</code> containing the default usage text and targetting range. When
     * using additional configuration settings in your skills, be sure to override this method to define them with
     * defaults.
     * 
     * @return a default configuration
     */
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty(SETTING_USETEXT, "%hero% used %skill% on %target%!");
        node.setProperty(SETTING_MAXDISTANCE, 15);
        return node;
    }

    /**
     * Handles target acquisition before calling {@link #use(Hero, LivingEntity, String[])}.
     * 
     * @param hero
     *            the {@link Hero} using the skill
     * @param args
     *            the arguments provided with the command
     * @return <code>true</code> if the skill executed properly, <code>false</code> otherwise
     */
    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int maxDistance = getSetting(hero.getHeroClass(), SETTING_MAXDISTANCE, 15);
        LivingEntity target = null;
        if (args.length > 0) {
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                Messaging.send(player, "Target not found.");
                return false;
            }
            if (target.getLocation().toVector().distance(player.getLocation().toVector()) > maxDistance) {
                Messaging.send(player, "Target is too far away.");
                return false;
            }
            if (!inLineOfSight(player, (Player) target)) {
                Messaging.send(player, "Sorry, target is not in your line of sight!");
                return false;
            }
        }
        if (target == null) {
            target = getPlayerTarget(player, maxDistance);
        } else {
            if (args.length > 1) {
                args = Arrays.copyOfRange(args, 1, args.length);
            }
        }
        if (target == null) {
            target = player;
        }
        return use(hero, target, args);
    }

    /**
     * The heart of any TargettedSkill, this method defines what actually happens when the skill is used.
     * 
     * @param hero
     *            the {@link Hero} using the skill
     * @param args
     *            the arguments provided with the command
     * @return <code>true</code> if the skill executed properly, <code>false</code> otherwise
     */
    public abstract boolean use(Hero hero, LivingEntity target, String[] args);

    /**
     * Returns the first LivingEntity in the line of sight of a Player.
     * 
     * @param player
     *            the player being checked
     * @param maxDistance
     *            the maximum distance to search for a target
     * @return the player's target or null if no target is found
     */
    public static LivingEntity getPlayerTarget(Player player, int maxDistance) {
        HashSet<Byte> transparent = new HashSet<Byte>();
        transparent.add((byte) Material.AIR.getId());
        transparent.add((byte) Material.WATER.getId());
        List<Block> lineOfSight = player.getLineOfSight(transparent, maxDistance);
        List<Entity> nearbyEntities = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity) {
                Location entityLocation = entity.getLocation();
                int entityX = entityLocation.getBlockX();
                int entityZ = entityLocation.getBlockZ();
                for (Block block : lineOfSight) {
                    Location blockLocation = block.getLocation();
                    if (entityX == blockLocation.getBlockX() && entityZ == blockLocation.getBlockZ()) {
                        return (LivingEntity) entity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper method to check whether a player is in another player's line of sight.
     * 
     * @param a
     *            the source
     * @param b
     *            the target
     * @return <code>true</code> if <code>b</code> is in <code>a</code>'s line of sight; <code>false</code> otherwise
     */
    public static boolean inLineOfSight(Player a, Player b) {
        if (a == b) {
            return true;
        }

        Location aLoc = a.getEyeLocation();
        Location bLoc = b.getEyeLocation();
        int distance = Location.locToBlock(aLoc.toVector().distance(bLoc.toVector())) - 1;
        if (distance > 120) {
            return false;
        }
        Vector ab = new Vector(bLoc.getX() - aLoc.getX(), bLoc.getY() - aLoc.getY(), bLoc.getZ() - aLoc.getZ());
        Iterator<Block> iterator = new BlockIterator(a.getWorld(), aLoc.toVector(), ab, 0, distance + 1);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            Material type = block.getType();
            if (type != Material.AIR && type != Material.WATER) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the pretty name of a <code>LivingEntity</code>.
     * 
     * @param entity
     *            the entity
     * @return the pretty name of the entity
     */
    public static String getEntityName(LivingEntity entity) {
        return entity instanceof Player ? ((Player) entity).getName() : entity.getClass().getSimpleName().substring(5);
    }

}
