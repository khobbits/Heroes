package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntitySmallFireball;
import net.minecraft.server.Vec3D;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.common.CombustEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillFireball extends ActiveSkill {

    public SkillFireball(Heroes plugin) {
        super(plugin, "Fireball");
        setDescription("Shoots a dangerous ball of fire");
        setUsage("/skill fireball");
        setArgumentRange(0, 0);
        setIdentifiers("skill fireball");

        setTypes(SkillType.FIRE, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(this), Priority.Normal);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DAMAGE.node(), 4);
        node.set("fire-ticks", 100);
        node.set(Setting.DEATH_TEXT.node(), "%target% was burned alive by %hero%'s fireball!");
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Block target = player.getTargetBlock(Util.transparentIds, 100);
        Location playerLoc = player.getLocation();

        double dx = target.getX() - playerLoc.getX();
        double height = 1;
        double dy = target.getY() + (height / 2.0F) - (playerLoc.getY() + (height / 2.0F));
        double dz = target.getZ() - playerLoc.getZ();

        EntityLiving playerEntity = ((CraftPlayer) player).getHandle();
        EntitySmallFireball fireball = new EntitySmallFireball(((CraftWorld) player.getWorld()).getHandle(), playerEntity, dx, dy, dz);
        fireball.isIncendiary = false;
        double d8 = 4D;
        Vec3D vec3d = Util.getLocation(player);
        fireball.locX = playerLoc.getX() + vec3d.a * d8;
        fireball.locY = playerLoc.getY() + (height / 2.0F) + 0.5D;
        fireball.locZ = playerLoc.getZ() + vec3d.c * d8;

        ((CraftWorld) player.getWorld()).getHandle().addEntity(fireball);

        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
        /*
        Player player = hero.getPlayer();

        Snowball snowball = player.throwSnowball();
        snowball.setFireTicks(1000);

        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
        */
    }

    public class SkillEntityListener extends EntityListener {

        private final Skill skill;

        public SkillEntityListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent) || !(event.getEntity() instanceof LivingEntity)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            Entity projectile = subEvent.getDamager();
            if (!(projectile instanceof SmallFireball)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            LivingEntity entity = (LivingEntity) subEvent.getEntity();
            Entity dmger = ((SmallFireball) projectile).getShooter();
            if (dmger instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) dmger);

                if (!damageCheck((Player) dmger, entity)) {
                    Heroes.debug.stopTask("HeroesSkillListener");
                    event.setCancelled(true);
                    return;
                }

                // Damage the player and ignite them.
                entity.setFireTicks(SkillConfigManager.getUseSetting(hero, skill, "fire-ticks", 100, false));
                if (entity instanceof Player) {
                    plugin.getHeroManager().getHero((Player) entity).addEffect(new CombustEffect(skill, (Player) dmger));
                } else
                    plugin.getEffectManager().addEntityEffect(entity, new CombustEffect(skill, (Player) dmger));
                
                addSpellTarget(entity, hero);
                int damage = SkillConfigManager.getUseSetting(hero, skill, Setting.DAMAGE, 4, false);
                event.setDamage(damage);
            }

            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
