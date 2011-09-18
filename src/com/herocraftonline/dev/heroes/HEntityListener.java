package com.herocraftonline.dev.heroes;

import java.util.Set;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.effects.CombustEffect;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Properties;
import com.herocraftonline.dev.heroes.util.Util;

public class HEntityListener extends EntityListener {

    private final Heroes plugin;

    public HEntityListener(Heroes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Entity defender = event.getEntity();
        Player attacker = null;
        EntityDamageEvent lastDamage = defender.getLastDamageCause();
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();
            if (damager instanceof Player) {
                attacker = (Player) damager;
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    attacker = (Player) projectile.getShooter();
                }
            }
        }

        Properties prop = plugin.getConfigManager().getProperties();
        if (defender instanceof Player) {

            // Incur 5% experience loss to dying player
            // 5% of the next level's experience requirement
            // Experience loss can optionally reduce Level
            Hero heroDefender = plugin.getHeroManager().getHero((Player) defender);
            double exp = heroDefender.getExperience();
            int level = prop.getLevel(exp);
            
            //check to see if this death was caused by FireTick
            if (attacker == null && heroDefender.hasEffect("Combust")) {
                attacker = ((CombustEffect) heroDefender.getEffect("Combust")).getApplier();
            }
            
            if(prop.resetOnDeath) {
                //Wipe xp if we are in hardcore mode
                heroDefender.gainExp(-heroDefender.getExperience(), ExperienceType.DEATH, false);
                heroDefender.changeHeroClass(plugin.getClassManager().getDefaultClass());
            } else {
                //otherwise just do standard loss
                int currentLevelExp = (int) prop.getExperience(level);
                int nextLevelExp = (int) prop.getExperience(level + 1);
                double expLossPercent = prop.expLoss;
                if(heroDefender.getHeroClass().getExpLoss() != -1) {
                    expLossPercent = heroDefender.getHeroClass().getExpLoss();
                }
                double expLoss = (nextLevelExp - currentLevelExp) * expLossPercent;
                heroDefender.gainExp(-expLoss, ExperienceType.DEATH, false);
            }
            
            //Always reset mana on death
            heroDefender.setMana(0);


            // Remove any nonpersistent effects
            for (Effect effect : heroDefender.getEffects()) {
                if (!effect.isPersistent()) {
                    heroDefender.removeEffect(effect);
                }
            }
        } else if (defender instanceof Creature) {
            if (attacker == null && plugin.getHeroManager().creatureHasEffect((Creature) defender, "Combust")) {
                attacker = ((CombustEffect) plugin.getHeroManager().getCreatureEffect((Creature) defender, "Combust")).getApplier();
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
                // Don't award XP for Players killing themselves
                if (!(defender.equals(attacker))) {
                    prop.playerDeaths.put((Player) defender, defender.getLocation());
                    addedExp = prop.playerKillingExp;
                    experienceType = ExperienceType.PVP;
                }
            }

            //If this entity is on the summon map, don't award XP!
            if (hero.getSummons().contains(defender))
                return;

            // If the Player killed a Monster/Animal then we check to see if they can earn EXP from KILLING.
            if (defender instanceof LivingEntity && !(defender instanceof Player) && expSources.contains(ExperienceType.KILLING)) {
                //Check if the kill was near a spawner
                if (plugin.getConfigManager().getProperties().noSpawnCamp && Util.isNearSpawner(defender, plugin.getConfigManager().getProperties().spawnCampRadius) ) {
                    return;
                }
                // Get the dying entity's CreatureType
                CreatureType type = Util.getCreatureFromEntity(defender);
                if (type != null && !hero.getSummons().contains(defender)) {
                    // If EXP hasn't been assigned for this Entity then we stop here.
                    if (!prop.creatureKillingExp.containsKey(type))
                        return;
                    addedExp = prop.creatureKillingExp.get(type);
                    experienceType = ExperienceType.KILLING;
                }
            }
            if (experienceType != null && addedExp > 0) {
                hero.gainExp(addedExp, experienceType);
            }
            // Make sure to remove any effects this creature may have had from the creatureEffect map
            if (defender instanceof Creature) {
                plugin.getHeroManager().clearCreatureEffects((Creature) defender);
            }
        }
    }
}
