package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MobEffect;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillPlagueBomb extends ActiveSkill {
    private Map<Integer, Player> sheepMap = new HashMap<Integer, Player>();

    private class SkillListener implements Listener {
        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            Entity entity = event.getEntity();

            if (entity instanceof Sheep) {
                if (event.getCause() != EntityDamageEvent.DamageCause.POISON) {
                    explodeSheep((Sheep) entity);
                }
            }
        }
    }

    public SkillPlagueBomb(Heroes plugin) {
        super(plugin, "PlagueBomb");
        setDescription("You spawn a diseased explosive sheep.");
        setUsage("/skill plaguebomb <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill plaguebomb");
        setTypes(SkillType.HARMFUL, SkillType.EARTH, SkillType.SUMMON);
        Bukkit.getPluginManager().registerEvents(new SkillListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DAMAGE.node(), 10);
        node.set("fuse-time", 5000);
        node.set("velocity", 1.0);
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
        return getDescription().replace("$1", damage + "");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        final Player player = hero.getPlayer();
        Vector pLoc = player.getLocation().toVector();
        Vector direction = player.getLocation().getDirection();
        Vector spawnLoc = pLoc.add(direction);
        final World world = player.getWorld();

        final LivingEntity sheep = world.spawnCreature(spawnLoc.toLocation(world), CreatureType.SHEEP);
        sheepMap.put(sheep.getEntityId(), player);

        EntityLiving cbSheep = ((CraftLivingEntity) sheep).getHandle();
        cbSheep.addEffect(new MobEffect(19, 10000, 0));
        cbSheep.setHealth(10000);

        double velocity = SkillConfigManager.getUseSetting(hero, this, "velocity", 1.0, false);
        sheep.setVelocity(direction.multiply(velocity).add(new Vector(0, 0.15, 0)));

        int fuse = SkillConfigManager.getUseSetting(hero, this, "fuse-time", 5000, true);
        final int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                explodeSheep(sheep);
            }
        }, fuse / 1000 * 20);

        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    private void explodeSheep(LivingEntity sheep) {
        int id = sheep.getEntityId();
        if (sheepMap.containsKey(id)) {
            Player player = sheepMap.get(id);
            Hero hero = plugin.getHeroManager().getHero(player);
            int damage = 10;
            if (hero != null) {
                damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 10, false);
            }

            if (!sheep.isDead()) {
                sheep.getWorld().createExplosion(sheep.getLocation(), 0.0F, false);
                sheep.damage(20000);

                List<Entity> nearby = sheep.getNearbyEntities(5, 5, 5);
                for (Entity entity : nearby) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.setNoDamageTicks(0);
                        damageEntity(livingEntity, player, damage, EntityDamageEvent.DamageCause.MAGIC);
                    }
                }
            }

            sheepMap.remove(id);
        }
    }
}
