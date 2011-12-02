package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillWolf extends ActiveSkill {

    public boolean skillTaming = true;
    public Set<Wolf> wolves = new HashSet<Wolf>();

    public SkillWolf(Heroes plugin) {
        super(plugin, "Wolf");
        setDescription("Summons and tames a wolf to your side");
        setUsage("/skill wolf <release|summon>");
        setArgumentRange(0, 1);
        setIdentifiers("skill wolf");
        setTypes(SkillType.SUMMON);

        SkillEntityListener seListener = new SkillEntityListener(this);
        SkillPlayerListener spListener = new SkillPlayerListener(this);

        registerEvent(Type.CHUNK_UNLOAD, new SkillChunkListener(), Priority.Highest);
        registerEvent(Type.ENTITY_TAME, seListener, Priority.Highest);
        registerEvent(Type.ENTITY_DEATH, seListener, Priority.Monitor);
        registerEvent(Type.PLAYER_JOIN, spListener, Priority.High);
        registerEvent(Type.PLAYER_QUIT, spListener, Priority.Lowest);
        registerEvent(Type.CUSTOM_EVENT, new SkillHeroListener(this), Priority.Highest);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.MAX_DISTANCE.node(), 5);
        node.set("max-wolves", 3);
        node.set(Setting.HEALTH.node(), 30);
        node.set("health-per-level", .25);
        node.set(Setting.DAMAGE.node(), 3);
        node.set("damage-per-level", .1);
        node.set("tame-requires-skill", true);
        return node;
    }

    @Override
    public void init() {
        super.init();
        skillTaming = getSetting(null, "tame-requires-skill", true);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        if (args.length == 0) {

            int wolves = 0;
            if (hero.getSkillSettings(this) != null) {
                wolves = Integer.parseInt(hero.getSkillSettings(this).get("wolves"));
            }

            int maxWolves = getSetting(hero, "max-wolves", 3, false);
            if (wolves >= maxWolves) {
                Messaging.send(player, "You already have the maximum number of summons.");
                return SkillResult.FAIL;
            }

            int distance = getSetting(hero, Setting.MAX_DISTANCE.node(), 5, false);
            Location castLoc = player.getTargetBlock((HashSet<Byte>) null, distance).getLocation();
            if (castLoc.getBlock().getType() != Material.AIR) {
                castLoc = castLoc.getBlock().getRelative(BlockFace.UP).getLocation();
            }

            if (castLoc.getBlock().getType() != Material.AIR) {
                Messaging.send(player, "No room to summon a wolf at that location!");
                return SkillResult.FAIL;
            }

            Wolf wolf = (Wolf) player.getWorld().spawnCreature(castLoc, CreatureType.WOLF);
            setWolfSettings(hero, wolf);
            hero.setSkillSetting(this, "wolves", wolves + 1);
            broadcastExecuteText(hero);
            return SkillResult.NORMAL;
        } else if (args[0].equals("summon")) {
            boolean summoned = false;
            for (Creature creature : hero.getSummons()) {
                if (creature instanceof Wolf) {
                    summoned = true;
                    creature.teleport(player);
                }
            }
            if (!summoned) {
                Messaging.send(player, "You have no wolves to summon.");
            } else {
                broadcast(player.getLocation(), "$1 has summoned wolves to their side!", player.getDisplayName());
                return SkillResult.NORMAL;
            }
        } else if (args[0].equals("release")) {
            Iterator<Creature> iter = hero.getSummons().iterator();
            while (iter.hasNext()) {
                Creature creature = iter.next();
                if (creature instanceof Wolf) {
                    iter.remove();
                    creature.remove();
                }
            }

            hero.setSkillSetting(this, "wolves", 0);
            broadcast(player.getLocation(), "$1 has released their wolves into the wild", player.getDisplayName());
        }

        return SkillResult.SKIP_POST_USAGE;
    }

    private void setWolfSettings(Hero hero, Wolf wolf) {
        Player player = hero.getPlayer();
        int health = getSetting(hero, Setting.HEALTH.node(), 30, false);
        health = (int) (health + getSetting(hero, "health-per-level", .25, false) * hero.getLevel(this));
        wolf.setOwner(player);
        wolf.setTamed(true);
        wolf.setHealth(health);
        hero.getSummons().add(wolf);
        wolves.add(wolf);
    }

    public class SkillEntityListener extends EntityListener {

        private final SkillWolf skill;

        SkillEntityListener(SkillWolf skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            if (!(event.getEntity() instanceof Wolf)) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Wolf wolf = (Wolf) event.getEntity();
            AnimalTamer owner = wolf.getOwner();
            if (!wolf.isTamed() || owner == null || !(owner instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Hero hero = plugin.getHeroManager().getHero((Player) owner);
            if (!hero.getSummons().contains(wolf)) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            hero.getSummons().remove(wolf);
            wolves.remove(wolf);
            int wolves = Integer.parseInt(hero.getSkillSettings(skill).get("wolves"));
            hero.setSkillSetting(skill, "wolves", wolves - 1);
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }

        @Override
        public void onEntityTame(EntityTameEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            AnimalTamer owner = event.getOwner();
            Entity animal = event.getEntity();
            if (event.isCancelled() || !(animal instanceof Wolf) || !(owner instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Player player = (Player) owner;
            Hero hero = plugin.getHeroManager().getHero(player);
            int numWolves = 0;
            for (Creature creature : hero.getSummons()) {
                if (creature instanceof Wolf) {
                    numWolves++;
                }
            }
            if (skill.skillTaming && !hero.hasSkill(skill.getName())) {
                event.setCancelled(true);
            } else {
                if (numWolves >= getSetting(hero, "max-wolves", 3, false)) {
                    event.setCancelled(true);
                    Messaging.send(player, "You can't tame anymore wolves!");
                    return;
                }
                skill.setWolfSettings(hero, (Wolf) animal);
                Messaging.send(player, "You have tamed a wolf!");
            }
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }
    }

    public class SkillHeroListener extends HeroesEventListener {

        private Skill skill;

        public SkillHeroListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onClassChange(ClassChangeEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Hero hero = event.getHero();

            Iterator<Creature> iter = hero.getSummons().iterator();
            while (iter.hasNext()) {
                Creature creature = iter.next();
                if (creature instanceof Wolf) {
                    creature.remove();
                    wolves.remove(creature);
                    iter.remove();
                }
            }
            Map<String, String> skillSettings = hero.getSkillSettings(skill);
            if (skillSettings != null) {
                hero.setSkillSetting(skill, "wolves", 0);
            }
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }

        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            if (!(event.getDamager() instanceof Wolf) || event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Wolf wolf = (Wolf) event.getDamager();
            AnimalTamer owner = wolf.getOwner();
            if (!wolf.isTamed() || owner == null || !(owner instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Hero hero = plugin.getHeroManager().getHero((Player) owner);
            if (!hero.getSummons().contains(wolf)) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            double damagePerLevel = skill.getSetting(hero, "damage-per-level", .1, false);
            int damage = skill.getSetting(hero, Setting.DAMAGE.node(), 3, false) + (int) (hero.getLevel(skill) * damagePerLevel);
            event.setDamage(damage);
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }
    }

    public class SkillChunkListener extends WorldListener {

        @Override
        public void onChunkUnload(ChunkUnloadEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Set<Wolf> wolvesClone = new HashSet<Wolf>(wolves);
            for (Wolf wolf : wolvesClone) {
                Location loc = wolf.getLocation();
                if (event.getChunk().getX() == (loc.getBlockX() >> 4) && (event.getChunk().getZ() == loc.getBlockZ() >> 4)) {
                    Player owner = (Player) wolf.getOwner();
                    if (owner.isOnline()) {
                        Wolf newWolf = (Wolf) owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.WOLF);
                        setWolfSettings(plugin.getHeroManager().getHero(owner), newWolf);
                        wolves.add(newWolf);
                    }
                    wolves.remove(wolf);
                    wolf.remove();
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }
    }

    public class SkillPlayerListener extends PlayerListener {

        private final Skill skill;

        public SkillPlayerListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onPlayerJoin(PlayerJoinEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            Player player = event.getPlayer();
            Hero hero = plugin.getHeroManager().getHero(player);

            if (!hero.hasSkill(skill) || hero.getSkillSettings(skill) == null) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            if (hero.getSkillSettings(skill).containsKey("wolves")) {
                int wolves = Integer.parseInt(hero.getSkillSettings(skill).get("wolves"));
                for (int i = 0; i < wolves; i++) {
                    Wolf wolf = (Wolf) player.getWorld().spawnCreature(player.getLocation(), CreatureType.WOLF);
                    setWolfSettings(hero, wolf);
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }

        @Override
        public void onPlayerQuit(PlayerQuitEvent event) {
            Heroes.debug.startTask("HeroesSkillListener.Wolf");
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasSkill("Wolf")) {
                Heroes.debug.stopTask("HeroesSkillListener.Wolf");
                return;
            }

            Iterator<Creature> iter = hero.getSummons().iterator();
            while (iter.hasNext()) {
                Creature creature = iter.next();
                if (creature instanceof Wolf) {
                    System.out.println("removing wolf");
                    creature.remove();
                    wolves.remove(creature);
                    iter.remove();
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener.Wolf");
        }
    }
}
