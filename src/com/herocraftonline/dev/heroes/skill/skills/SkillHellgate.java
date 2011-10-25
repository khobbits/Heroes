package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillHellgate extends ActiveSkill {

    public SkillHellgate(Heroes plugin) {
        super(plugin, "Hellgate");
        setDescription("Teleports you and your nearby party to or from the nether");
        setUsage("/skill hellgate");
        setArgumentRange(0, 0);
        setIdentifiers("skill hellgate");
        setTypes(SkillType.SILENCABLE, SkillType.TELEPORT);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.RADIUS.node(), 10);
        node.setProperty("hell-world", "world_nether");
        node.setProperty("default-return", "world"); // default world the player return to if their location wasn't
                                                     // saved
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        String defaultWorld = getSetting(hero, "default-return", "world");
        String hellWorld = getSetting(hero, "hell-world", "world_nether");
        World world = null;
        Location teleportLocation = null;
        Location castLocation = player.getLocation();

        if (hero.hasEffect("Hellgate")) {
            teleportLocation = ((HellgateEffect) hero.getEffect("Hellgate")).getLocation();
            player.teleport(teleportLocation);
            hero.removeEffect(hero.getEffect("Hellgate"));
        } else if (player.getWorld().getEnvironment() == Environment.NETHER) {
            // If the player doesn't have the Hellgate effect and is on nether - return them to spawn on the default
            // world
            world = plugin.getServer().getWorld(defaultWorld);
            if (world == null) {
                world = plugin.getServer().getWorlds().get(0);
            }
            player.teleport(world.getSpawnLocation());
        } else {
            // We are on the main world so lets setup a teleport to nether!
            world = plugin.getServer().getWorld(hellWorld);
            if (world == null) {
                for (World tWorld : plugin.getServer().getWorlds()) {
                    if (tWorld.getEnvironment() == Environment.NETHER) {
                        world = tWorld;
                    }
                }
            }
            // If world is still null then there is no world to teleport to
            if (world == null) {
                Messaging.send(player, "No world to open a Hellgate into!");
                return false;
            }

            hero.addEffect(new HellgateEffect(this, player.getLocation()));
            player.teleport(world.getSpawnLocation());
        }

        if (hero.hasParty()) {
            int rangeSquared = (int) Math.pow(getSetting(hero, Setting.RADIUS.node(), 10, false), 2);
            for (Hero targetHero : hero.getParty().getMembers()) {
                Player target = targetHero.getPlayer();
                if (target.equals(player)) {
                    continue;
                }
                if (!castLocation.getWorld().equals(target.getWorld())) {
                    continue;
                }
                if (castLocation.distanceSquared(target.getLocation()) > rangeSquared) {
                    continue;
                }

                if (targetHero.hasEffect("Hellgate")) {
                    HellgateEffect hEffect = (HellgateEffect) targetHero.getEffect("Hellgate");
                    target.teleport(hEffect.getLocation());
                    targetHero.removeEffect(hEffect);
                } else {
                    target.teleport(world.getSpawnLocation());
                    // If we teleported to a hell-world lets add the effect
                    if (world.getEnvironment() == Environment.NETHER) {
                        targetHero.addEffect(new HellgateEffect(this, target.getLocation()));
                    }
                }
            }
        }

        broadcastExecuteText(hero);
        return true;
    }

    // Tracks the players original location for returning
    public class HellgateEffect extends Effect {

        private final Location location;

        public HellgateEffect(Skill skill, Location location) {
            super(skill, "Hellgate");
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

    }
}
