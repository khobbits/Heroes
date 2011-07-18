package com.herocraftonline.dev.heroes;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

public class HEntityListener extends EntityListener {

    private static final DecimalFormat decFormat = new DecimalFormat("#0.##");

    private final Heroes plugin;
    private HashMap<Integer, Player> kills = new HashMap<Integer, Player>();

    public HEntityListener(Heroes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        Entity defender = event.getEntity();
        if (defender instanceof LivingEntity) {
            if (((LivingEntity) defender).getHealth() - event.getDamage() <= 0) {
                // Grab the Attacker regardless of the Event.
                Entity attacker = null;
                if (event instanceof EntityDamageByProjectileEvent) {
                    EntityDamageByProjectileEvent subEvent = (EntityDamageByProjectileEvent) event;
                    attacker = subEvent.getDamager();
                } else if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                    attacker = subEvent.getDamager();
                }
                // If it's a legitimate attack then we add it to the Kills list.
                if (attacker != null && attacker instanceof Player) {
                    kills.put(defender.getEntityId(), (Player) attacker);
                } else {
                    kills.remove(defender.getEntityId());
                }
            }
        }

    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Entity defender = event.getEntity();
        Player attacker = kills.get(defender.getEntityId());
        kills.remove(defender.getEntityId());

        Properties prop = plugin.getConfigManager().getProperties();
        if (defender instanceof Player) {
            // Incur 5% experience loss to dying player
            // 5% of the next level's experience requirement
            // Experience loss can't reduce level
            Hero heroDefender = plugin.getHeroManager().getHero((Player) defender);
            double exp = heroDefender.getExperience();
            int level = prop.getLevel(exp);
            if (level < prop.maxLevel) {
                int currentLevelExp = (int) prop.getExperience(level);
                int nextLevelExp = (int) prop.getExperience(level + 1);
                double expLoss = (nextLevelExp - currentLevelExp) * prop.expLoss;
                if (exp - expLoss < currentLevelExp) {
                    expLoss = exp - currentLevelExp;
                }
                heroDefender.setExperience(exp - expLoss);
                heroDefender.setMana(0);
                heroDefender.setHealth(heroDefender.getMaxHealth());
                Messaging.send(heroDefender.getPlayer(), "You have lost " + decFormat.format(expLoss) + " exp for dying.");
            }

            // Remove any nonpersistent effects
            for (Effect effect : heroDefender.getEffects()) {
                if (!effect.isPersistent()) {
                    heroDefender.removeEffect(effect);
                }
            }
        }

        if (attacker != null) {
            // Get the Hero representing the player
            Hero hero = plugin.getHeroManager().getHero(attacker);
            // Get the player's class definition
            HeroClass playerClass = hero.getHeroClass();
            // Get the sources of experience for the player's class
            Set<ExperienceType> expSources = playerClass.getExperienceSources();

            double addedExp = 0;
            ExperienceType experienceType = null;

            // If the Player killed another Player we check to see if they can earn EXP from PVP.
            if (defender instanceof Player && expSources.contains(ExperienceType.PVP)) {
                prop.playerDeaths.put((Player) defender, defender.getLocation());
                addedExp = prop.playerKillingExp;
                experienceType = ExperienceType.PVP;
            }

            // If the Player killed a Monster/Animal then we check to see if they can earn EXP from KILLING.
            if (defender instanceof LivingEntity && !(defender instanceof Player) && expSources.contains(ExperienceType.KILLING)) {
                // Get the dying entity's CreatureType
                CreatureType type = null;
                try {
                    Class<?>[] interfaces = defender.getClass().getInterfaces();
                    for (Class<?> c : interfaces) {
                        if (LivingEntity.class.isAssignableFrom(c)) {
                            type = CreatureType.fromName(c.getSimpleName());
                            break;
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                }
                if (type != null) {
                    // If EXP hasn't been assigned for this Entity then we stop here.
                    if (!prop.creatureKillingExp.containsKey(type)) {
                        return;
                    }
                    addedExp = prop.creatureKillingExp.get(type);
                    experienceType = ExperienceType.KILLING;
                }
            }
            if (experienceType != null && addedExp > 0) {
                hero.gainExp(addedExp, experienceType);
            }
        }
    }
}
