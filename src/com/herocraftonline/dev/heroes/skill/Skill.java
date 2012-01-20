package com.herocraftonline.dev.heroes.skill;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

/**
 * The root class of the skill heirarchy. This class implements the basic functionality of every Heroes skill including
 * configuration handling, area-based player notifications and event registration. Because this class extends
 * {@link com.herocraftonline.dev.heroes.command.BaseCommand}, the constructor of every skill should define a name,
 * description, usage, min and max arguments and at least one identifier. Any registered events must provide an event
 * listener, usually created as an inner class.
 * </br>
 * </br>
 * <b>Skill Framework:</b>
 * <ul>
 * <li>{@link ActiveSkill}</li>
 * <ul>
 * <li>{@link TargettedSkill}</li>
 * </ul>
 * <li>{@link PassiveSkill}</li> <li>{@link OutsourcedSkill}</li> </ul>
 * <b>Note:</b> All skill identifiers <i>must</i> begin with <i>skill</i>, e.g. "skill fireball".
 */
public abstract class Skill extends BasicCommand {

    public final Heroes plugin;
    private Configuration defaultConfig = new MemoryConfiguration();

    private final Set<SkillType> types = EnumSet.noneOf(SkillType.class);

    /**
     * The constructor of every skill must define:
     * <ul>
     * <li><code>name</code></li>
     * <li><code>description</code></li>
     * <li><code>usage</code></li>
     * <li><code>minArgs</code></li>
     * <li><code>maxArgs</code></li>
     * <li><code>identifiers</code></li>
     * <li><code>notes</code> (optional)</li>
     * </ul>
     * 
     * @param plugin
     *            the active Heroes instance
     */
    public Skill(Heroes plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    /**
     * Gets hero-specific description of the skill.  For use with level-based skill data.
     * @param hero
     * @return
     */
    public abstract String getDescription(Hero hero);
    
    public void addSpellTarget(Entity o, Hero hero) {
        plugin.getDamageManager().addSpellTarget(o, hero, this);
    }

    /**
     * Helper method that broadcasts a message to all players within 30 blocks of the specified source. These messages
     * can be suppressed by players on an individual basis.
     * 
     * @param source
     *            the <code>Location</code> to measure from
     * @param message
     *            the content of the message
     * @param args
     *            any text in the message of the format $<i>n</i> where <i>n</i>
     *            is an integer will be replaced with the <i>n</i>th element of
     *            this array
     */
    public void broadcast(Location source, String message, Object... args) {
        if (message == null || message.isEmpty() || message.equalsIgnoreCase("off")) {
            return;
        }

        final Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.isSuppressing(this)) {
                continue;
            }
            if (source.getWorld().equals(playerLocation.getWorld())) {
                if (playerLocation.distance(source) < 30) {
                    Messaging.send(player, message, args);
                }
            }
        }
    }

    /**
     * Tests if the target is damagable from a source. Also adds a last-damage cause to the target which allows proper
     * xp.
     * Returns if the damage check was successful.
     * 
     * @param player
     * @param target
     * @return
     */
    public boolean damageCheck(Player player, LivingEntity target) {
        if (player.equals(target))
            return false;

        EntityDamageByEntityEvent damageEntityEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 0);
        plugin.getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled())
            return false;

        //Reverse damage check to make sure the target can damage the player - this prevents the player from attacking the target while invulnerable
        damageEntityEvent = new EntityDamageByEntityEvent(target, player, DamageCause.CUSTOM, 0);
        plugin.getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled())
            return false;
        return true;
    }

    /**
     * The end of the execution path of a skill, this method is called whenever a command with a registered identifier
     * is used.
     * 
     * @param sender
     *            the <code>CommandSender</code> issuing the command
     * @param args
     *            the arguments provided with the command
     */
    @Override
    public abstract boolean execute(CommandSender sender, String identifier, String[] args);

    /**
     * Creates and returns a <code>ConfigurationNode</code> containing all the default data for the skill. By default,
     * this configuration is empty.
     * 
     * @return an empty configuration
     */
    public ConfigurationSection getDefaultConfig() {
        return defaultConfig;
    }

    public Set<SkillType> getTypes() {
        return Collections.unmodifiableSet(this.types);
    }

    /**
     * An initialization method called after all configuration data is loaded.
     */
    public abstract void init();

    @Override
    public boolean isShownOnHelpMenu() {
        return false;
    }

    public boolean isType(SkillType type) {
        return types.contains(type);
    }

    protected void setTypes(SkillType... types) {
        this.types.addAll(Arrays.asList(types));
    }
    
    public boolean damageEntity(LivingEntity target, LivingEntity attacker, int damage, DamageCause cause) {
        //Do it ourselves cause bukkit is stubborn
        ((CraftLivingEntity) target).setNoDamageTicks(0);
        EntityDamageByEntityEvent edbe = new EntityDamageByEntityEvent(attacker, target, cause, damage);
        plugin.getServer().getPluginManager().callEvent(edbe);
        if (edbe.isCancelled())
            return false;
        
        //Reset the ticks again just in case n.m.s is stubborn too
        ((CraftLivingEntity) target).setNoDamageTicks(0);
        // Issue the damage
        target.damage(edbe.getDamage(), attacker);
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj.toString().equals(getName()))
            return true;
        else if (!(obj instanceof Skill))
            return false;
        
        Skill s = (Skill) obj;
        return s.getName().equals(getName());
    }
    
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
