package com.herocraftonline.dev.heroes.damage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillUseInfo;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Properties;

public class DamageManager {

    private Heroes plugin;

    private HeroesDamageListener listener;
    private Map<Material, Integer> itemDamage;
    private Map<ProjectileType, Integer> projectileDamage;
    private Map<CreatureType, Integer> creatureHealth;
    private Map<CreatureType, Integer> creatureDamage;
    private Map<DamageCause, Integer> environmentalDamage;
    private Map<Entity, SkillUseInfo> spellTargs = new HashMap<Entity, SkillUseInfo>();

    public DamageManager(Heroes plugin) {
        this.plugin = plugin;
        listener = new HeroesDamageListener(plugin, this);
    }

    public Map<Entity, SkillUseInfo> getSpellTargets() {
        return spellTargs;
    }

    public void removeSpellTarget(Entity o) {
        spellTargs.remove(o);
    }

    public void addSpellTarget(Entity o, Hero hero, Skill skill) {
        SkillUseInfo skillInfo = new SkillUseInfo(hero, skill);
        spellTargs.put(o, skillInfo);
    }

    public Integer getCreatureDamage(CreatureType type) {
        return creatureDamage.get(type);
    }

    public Integer getCreatureHealth(CreatureType type) {
        if (creatureHealth.containsKey(type)) {
            int health = creatureHealth.get(type);
            return health > 200 ? 200 : health < 0 ? 0 : health;
        } else {
            return null;
        }
    }

    public Integer getEnvironmentalDamage(DamageCause cause) {
        return environmentalDamage.get(cause);
    }

    public Integer getItemDamage(Material item, HumanEntity entity) {
        if (entity != null && entity instanceof Player) {
            HeroClass heroClass = plugin.getHeroManager().getHero((Player) entity).getHeroClass();
            Integer classDamage = heroClass.getItemDamage(item);
            if (classDamage != null) {
                return classDamage;
            }
        }
        return itemDamage.get(item);
    }

    public Integer getProjectileDamage(ProjectileType type, HumanEntity entity) {
        if (entity != null && entity instanceof Player) {
            HeroClass heroClass = plugin.getHeroManager().getHero((Player) entity).getHeroClass();
            Integer classDamage = heroClass.getProjectileDamage(type);
            if (classDamage != null) {
                return classDamage;
            }
        }
        return projectileDamage.get(type);
    }

    public void load(Configuration config) {
        List<String> keys;

        creatureHealth = new HashMap<CreatureType, Integer>();
        keys = config.getKeys("creature-health");
        if (keys != null) {
            for (String key : keys) {
                CreatureType type = CreatureType.fromName(key);
                int health = config.getInt("creature-health." + key, 10);
                if (type != null) {
                    creatureHealth.put(type, health);
                }
            }
        }

        creatureDamage = new HashMap<CreatureType, Integer>();
        keys = config.getKeys("creature-damage");
        if (keys != null) {
            for (String key : keys) {
                CreatureType type = CreatureType.fromName(key);
                int damage = config.getInt("creature-damage." + key, 10);
                if (type != null) {
                    creatureDamage.put(type, damage);
                }
            }
        }

        itemDamage = new HashMap<Material, Integer>();
        keys = config.getKeys("item-damage");
        if (keys != null) {
            for (String key : keys) {
                Material item = Material.matchMaterial(key);
                int damage = config.getInt("item-damage." + key, 2);
                if (item != null) {
                    itemDamage.put(item, damage);
                }
            }
        }

        environmentalDamage = new HashMap<DamageCause, Integer>();
        keys = config.getKeys("environmental-damage");
        if (keys != null) {
            for (String key : keys) {
                try {
                    DamageCause cause = DamageCause.valueOf(key.toUpperCase());
                    int damage = config.getInt("environmental-damage." + key, 0);
                    environmentalDamage.put(cause, damage);
                } catch (IllegalArgumentException e) {
                }
            }
        }

        projectileDamage = new HashMap<ProjectileType, Integer>();
        keys = config.getKeys("projectile-damage");
        if (keys != null) {
            for (String key : keys) {
                try {
                    ProjectileType type = ProjectileType.valueOf(key.toUpperCase());
                    int damage = config.getInt("projectile-damage." + key, 0);
                    projectileDamage.put(type, damage);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    /**
     * Register the events for the damage system
     */
    public void registerEvents() {
        Properties prop = plugin.getConfigManager().getProperties();
        if (prop.damageSystem) {
            PluginManager pluginManager = plugin.getServer().getPluginManager();
            pluginManager.registerEvent(Type.ENTITY_DAMAGE, listener, Priority.High, plugin);
            pluginManager.registerEvent(Type.ENTITY_REGAIN_HEALTH, listener, Priority.Highest, plugin);
            pluginManager.registerEvent(Type.CREATURE_SPAWN, listener, Priority.Highest, plugin);
        }
    }

    public enum ProjectileType {
        ARROW,
        SNOWBALL,
        EGG;

        public static ProjectileType valueOf(Entity entity) {
            if (entity instanceof Arrow) {
                return ARROW;
            } else if (entity instanceof Snowball) {
                return SNOWBALL;
            } else if (entity instanceof Egg) {
                return EGG;
            } else {
                throw new IllegalArgumentException(entity.getClass().getSimpleName() + " is not a projectile.");
            }
        }
    }

}
