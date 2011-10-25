package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;

import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.common.CombustEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillExplosiveFireball extends ActiveSkill {

    public SkillExplosiveFireball(Heroes plugin) {
        super(plugin, "ExplosiveFireball");
        setDescription("Shoots an explosive ball of fire");
        setUsage("/skill fireball");
        setArgumentRange(0, 0);
        setIdentifiers("skill explosivefireball");
        setTypes(SkillType.FIRE, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(this), Priority.Low);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 4);
        node.setProperty("fire-ticks", 100);
        return node;
    }

    public Vec3D getLocation(Player player, float f) {
        Location playerLoc = player.getLocation();
        float rotationYaw = playerLoc.getYaw();
        float rotationPitch = playerLoc.getPitch();
        float prevRotationYaw = playerLoc.getYaw();
        float prevRotationPitch = playerLoc.getPitch();
        if (f == 1.0F) {
            float f1 = MathHelper.cos(-rotationYaw * 0.01745329F - 3.141593F);
            float f3 = MathHelper.sin(-rotationYaw * 0.01745329F - 3.141593F);
            float f5 = -MathHelper.cos(-rotationPitch * 0.01745329F);
            float f7 = MathHelper.sin(-rotationPitch * 0.01745329F);
            return Vec3D.create(f3 * f5, f7, f1 * f5);
        } else {
            float f2 = prevRotationPitch + (rotationPitch - prevRotationPitch) * f;
            float f4 = prevRotationYaw + (rotationYaw - prevRotationYaw) * f;
            float f6 = MathHelper.cos(-f4 * 0.01745329F - 3.141593F);
            float f8 = MathHelper.sin(-f4 * 0.01745329F - 3.141593F);
            float f9 = -MathHelper.cos(-f2 * 0.01745329F);
            float f10 = MathHelper.sin(-f2 * 0.01745329F);
            return Vec3D.create(f8 * f9, f10, f6 * f9);
        }
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        Block target = player.getTargetBlock((HashSet<Byte>) null, 100);
        Location playerLoc = player.getLocation();

        double dx = target.getX() - playerLoc.getX();
        double height = 1;
        double dy = target.getY() + (height / 2.0F) - (playerLoc.getY() + (height / 2.0F));
        double dz = target.getZ() - playerLoc.getZ();

        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityLiving playerEntity = craftPlayer.getHandle();
        EntityFireball fireball = new EntityFireball(((CraftWorld) player.getWorld()).getHandle(), playerEntity, dx, dy, dz);
        fireball.isIncendiary = false;
        double d8 = 4D;
        Vec3D vec3d = getLocation(player, 1.0F);
        fireball.locX = playerLoc.getX() + vec3d.a * d8;
        fireball.locY = playerLoc.getY() + (height / 2.0F) + 0.5D;
        fireball.locZ = playerLoc.getZ() + vec3d.c * d8;

        ((CraftWorld) player.getWorld()).getHandle().addEntity(fireball);

        broadcastExecuteText(hero);
        return true;
    }

    public class SkillEntityListener extends EntityListener {

        private final Skill skill;

        public SkillEntityListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                Entity attacker = subEvent.getDamager();
                if (attacker instanceof CraftFireball) {
                    CraftFireball fireball = (CraftFireball) attacker;
                    if (fireball.getShooter() instanceof Player) {
                        Entity entity = event.getEntity();
                        Player shooter = (Player) fireball.getShooter();
                        Hero hero = plugin.getHeroManager().getHero(shooter);
                        int damage = getSetting(hero, Setting.DAMAGE.node(), 4, false);
                        addSpellTarget(entity, hero);
                        entity.setFireTicks(getSetting(hero, "fire-ticks", 100, false));
                        if (entity instanceof Player) {
                            plugin.getHeroManager().getHero((Player) entity).addEffect(new CombustEffect(skill, shooter));
                        } else if (entity instanceof Creature) {
                            plugin.getEffectManager().addCreatureEffect((Creature) entity, new CombustEffect(skill, shooter));
                        }
                        event.setDamage(damage);
                    }
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
