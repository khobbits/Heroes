package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.MathHelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillFireball extends ActiveSkill {

    public SkillFireball(Heroes plugin) {
        super(plugin);
        setName("Fireball");
        setDescription("Shoots a dangerous ball of fire");
        setUsage("/skill fireball");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill fireball");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 4);
        node.setProperty("fire-ticks", 100);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Location location = player.getEyeLocation();

        float pitch = location.getPitch() / 180.0F * 3.1415927F;
        float yaw = location.getYaw() / 180.0F * 3.1415927F;

        double motX = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        double motZ = MathHelper.cos(yaw) * MathHelper.cos(pitch);
        double motY = -MathHelper.sin(pitch);
        Vector velocity = new Vector(motX, motY, motZ);

        Snowball snowball = player.throwSnowball();
        snowball.setFireTicks(1000);
        snowball.setVelocity(velocity);

        broadcastExecuteText(hero);
        return true;
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled()) return;
            if (event instanceof EntityDamageByProjectileEvent) {
                EntityDamageByProjectileEvent subEvent = (EntityDamageByProjectileEvent) event;
                Entity projectile = subEvent.getProjectile();
                if (projectile instanceof Snowball) {
                    if (projectile.getFireTicks() > 0) {
                        Entity entity = subEvent.getEntity();
                        if (entity instanceof LivingEntity) {
                            Entity dmger = subEvent.getDamager();
                            if (dmger instanceof Player) {
                                Hero hero = plugin.getHeroManager().getHero((Player) dmger);
                                HeroClass heroClass = hero.getHeroClass();
                                // Perform a check to see if any plugin is preventing us from damaging the player.
                                EntityDamageEvent damageEvent = new EntityDamageEvent(dmger, DamageCause.CUSTOM, 0);
                                Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                                if (damageEvent.isCancelled()) return;
                                // Damage the player and ignite them.
                                LivingEntity livingEntity = (LivingEntity) entity;
                                livingEntity.setFireTicks(getSetting(heroClass, "fire-ticks", 100));

                                // PROBLEM! To get the following statement to work, I need to specify the damaging
                                // player. However, because the DamageCause is necessarily ENTITY_ATTACK, we have no way
                                // of distinguishing this damage from melee damage. Therefore, the damage system changes
                                // the event damage based on what the damaging player is holding.
                                //
                                // The other options are:
                                // 1) Don't include the entity. In this case the damage is applied, but no event is
                                // thrown and the damage doesn't stick (our system reverts it).
                                // 2) Set the event damage rather than damage the entity. In this case the damage is
                                // doubled because two events are thrown. Even if this worked, we would end up having
                                // the same problem in as in (1) once we can modify projectile damage.
                                //
                                // The only reasonable way I see around this is a damage method that lets us specify the
                                // DamageCause of the event produced (or just one that is always CUSTOM).
                                livingEntity.damage(getSetting(heroClass, "damage", 4));

                            }
                        }
                    }
                }
            }
        }

    }

}
