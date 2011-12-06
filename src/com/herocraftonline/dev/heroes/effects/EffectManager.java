package com.herocraftonline.dev.heroes.effects;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;

public class EffectManager {

    private Set<ManagedEffect> managedEffects = new HashSet<ManagedEffect>();
    private Set<ManagedEffect> pendingRemovals = new HashSet<ManagedEffect>();
    private Set<ManagedEffect> pendingAdditions = new HashSet<ManagedEffect>();
    private Map<LivingEntity, Set<Effect>> entityEffects = new HashMap<LivingEntity, Set<Effect>>();
    private final static int effectInterval = 2;

    public EffectManager(Heroes plugin) {
        Runnable effectTimer = new EffectUpdater();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, effectTimer, 0, effectInterval);
    }

    public void manageEffect(Hero hero, Effect effect) {
        if (effect instanceof Expirable || effect instanceof Periodic)
            pendingAdditions.add(new ManagedHeroEffect(hero, effect));
    }

    public void manageEffect(LivingEntity lEntity, Effect effect) {
        if (effect instanceof Expirable || effect instanceof Periodic)
            pendingAdditions.add(new ManagedEntityEffect(lEntity, effect));
    }

    public void queueForRemoval(Hero hero, Effect effect) {
        ManagedEffect mEffect = new ManagedHeroEffect(hero, effect);
        if (managedEffects.contains(mEffect))
            pendingRemovals.add(mEffect);
    }

    public void queueForRemoval(LivingEntity lEntity, Effect effect) {
        ManagedEffect mEffect = new ManagedEntityEffect(lEntity, effect);
        if (managedEffects.contains(mEffect))
            pendingRemovals.add(mEffect);
    }

    public void addEntityEffect(LivingEntity lEntity, Effect effect) {
        Set<Effect> effects = entityEffects.get(lEntity);
        if (effects == null) {
            effects = new HashSet<Effect>();
            entityEffects.put(lEntity, effects);
        }

        if (effect instanceof Periodic || effect instanceof Expirable) {
            manageEffect(lEntity, effect);
        }

        effects.add(effect);
        effect.apply(lEntity);
    }

    public void manualRemoveEntityEffect(LivingEntity lEntity, Effect effect) {
        if (effect == null)
            return;
        Set<Effect> effects = entityEffects.get(lEntity);
        if (effects != null) {
            if (effect instanceof Expirable || effect instanceof Periodic) {
                queueForRemoval(lEntity, effect);
            }
            effects.remove(effect);
            if (effects.isEmpty()) {
                entityEffects.remove(lEntity);
            }
        }
    }
    
    public void removeEntityEffect(LivingEntity lEntity, Effect effect) {
        if (effect == null)
            return;
        Set<Effect> effects = entityEffects.get(lEntity);
        if (effects != null) {
            if (effect instanceof Expirable || effect instanceof Periodic) {
                queueForRemoval(lEntity, effect);
            }
            effect.remove(lEntity);
            effects.remove(effect);
            if (effects.isEmpty()) {
                entityEffects.remove(lEntity);
            }
        }
    }

    public void clearEntityEffects(LivingEntity lEntity) {
        if (entityEffects.containsKey(lEntity)) {
            Iterator<Effect> iter = entityEffects.get(lEntity).iterator();
            while (iter.hasNext()) {
                Effect effect = iter.next();
                if (effect instanceof Expirable || effect instanceof Periodic) {
                    queueForRemoval(lEntity, effect);
                } else {
                    effect.remove(lEntity);
                    iter.remove();
                }
            }
            entityEffects.remove(lEntity);
        }
    }

    public boolean entityHasEffectType(LivingEntity lEntity, EffectType type) {
        if (!entityEffects.containsKey(lEntity))
            return false;
        
        for (Effect effect : entityEffects.get(lEntity)) {
            if (effect.isType(type))
                return true;
        }
        
        return false;
    }
    
    public boolean entityHasEffect(LivingEntity lEntity, String name) {
        if (!entityEffects.containsKey(lEntity))
            return false;

        for (Effect effect : entityEffects.get(lEntity)) {
            if (effect.getName().equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    public Effect getEntityEffect(LivingEntity lEntity, String name) {
        Set<Effect> effects = entityEffects.get(lEntity);
        if (effects == null)
            return null;

        for (Effect effect : effects) {
            if (effect.getName().equalsIgnoreCase(name))
                return effect;
        }

        return null;
    }

    public Set<Effect> getEntityEffects(LivingEntity lEntity) {
        if (!entityEffects.containsKey(lEntity))
            return new HashSet<Effect>();

        return Collections.unmodifiableSet(entityEffects.get(lEntity));
    }

    class EffectUpdater implements Runnable {

        @Override
        public void run() {
            Heroes.debug.startTask("EffectUpdater.run");
            Set<ManagedEffect> removals = new HashSet<ManagedEffect>(pendingRemovals);
            pendingRemovals.clear();
            for (ManagedEffect managed : removals) {
                managedEffects.remove(managed);
            }

            Set<ManagedEffect> additions = new HashSet<ManagedEffect>(pendingAdditions);
            pendingAdditions.clear();
            for (ManagedEffect managed : additions) {
                managedEffects.add(managed);
            }

            for (ManagedEffect managed : managedEffects) {
                if (managed.effect instanceof Expirable) {
                    if (((Expirable) managed.effect).isExpired()) {
                        if (managed instanceof ManagedHeroEffect) {
                            ((ManagedHeroEffect) managed).hero.removeEffect(managed.effect);
                            continue;
                        } else {
                            removeEntityEffect(((ManagedEntityEffect) managed).lEntity, managed.effect);
                            continue;
                        }
                    }
                }
                if (managed.effect instanceof Periodic) {
                    Periodic periodic = (Periodic) managed.effect;
                    if (managed instanceof ManagedHeroEffect) {
                        if (periodic.isReady())
                            periodic.tick(((ManagedHeroEffect) managed).hero);
                    } else {
                        if (periodic.isReady())
                            periodic.tick(((ManagedEntityEffect) managed).lEntity);
                    }
                }
            }
            Heroes.debug.stopTask("EffectUpdater.run");
        }

    }

}
