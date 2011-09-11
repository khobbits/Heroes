package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPiggify extends TargettedSkill {

    private Set<Entity> creatures = new HashSet<Entity>();

    public SkillPiggify(Heroes plugin) {
        super(plugin, "Piggify");
        setDescription("Forces your target to ride a pig");
        setUsage("/skill piggify <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill piggify" });

        setTypes(SkillType.DEBUFF, SkillType.SILENCABLE);
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target.equals(player) || creatures.contains(target)) {
            Messaging.send(player, "You need a target.");
            return false;
        }

        // Throw a dummy damage event to make it obey PvP restricting plugins
        if (target instanceof Player) {
            EntityDamageEvent event = new EntityDamageByEntityEvent(player, target, DamageCause.ENTITY_ATTACK, 0);
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
        }

        CreatureType type = CreatureType.PIG;
        if (target.getLocation().getBlock().getType() == Material.WATER) {
            type = CreatureType.SQUID;
        }

        Entity creature = target.getWorld().spawnCreature(target.getLocation(), type);
        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        PigEffect pEffect = new PigEffect(this, duration, (Creature) creature);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(pEffect);
        } else if (target instanceof Creature) {
            plugin.getHeroManager().addCreatureEffect((Creature) target, pEffect);
        } else {
            Messaging.send(player, "Invalid Target!");
            return false;
        }
        broadcastExecuteText(hero, target);
        return true;
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !creatures.contains(event.getEntity()))
                return;

            event.setCancelled(true);
        }
    }

    public class PigEffect extends ExpirableEffect {

        private final Creature creature;

        public PigEffect(Skill skill, long duration, Creature creature) {
            super(skill, "Piggify", duration);
            this.creature = creature;
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.HARMFUL);
            this.types.add(EffectType.DISABLE);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            creature.setPassenger(player);
            creatures.add(creature);
        }

        @Override
        public void apply(Creature rider) {
            super.apply(rider);
            creature.setPassenger(rider);
            creatures.add(creature);
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            creatures.remove(creature);
            creature.remove();
        }

        @Override
        public void remove(Creature rider) {
            super.remove(rider);
            creatures.remove(creature);
            creature.remove();
        }
    }
}
