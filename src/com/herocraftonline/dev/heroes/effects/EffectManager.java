package com.herocraftonline.dev.heroes.effects;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;

public class EffectManager {

    private Set<ManagedEffect> managedEffects = new HashSet<ManagedEffect>();
    private Set<ManagedEffect> pendingRemovals = new HashSet<ManagedEffect>();
    private Set<ManagedEffect> pendingAdditions = new HashSet<ManagedEffect>();
    private Map<Creature, Set<Effect>> creatureEffects = new HashMap<Creature, Set<Effect>>();
    private final static int effectInterval = 2;

    public EffectManager(Heroes plugin) {
        Runnable effectTimer = new EffectUpdater();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, effectTimer, 0, effectInterval);
    }

    public void manageEffect(Hero hero, Effect effect) {
        if (effect instanceof Expirable || effect instanceof Periodic)
            pendingAdditions.add(new ManagedHeroEffect(hero, effect));
    }

    public void manageEffect(Creature creature, Effect effect) {
        if (effect instanceof Expirable || effect instanceof Periodic)
            pendingAdditions.add(new ManagedCreatureEffect(creature, effect));
    }

    public void queueForRemoval(Hero hero, Effect effect) {
        ManagedEffect mEffect = new ManagedHeroEffect(hero, effect);
        if (managedEffects.contains(mEffect))
            pendingRemovals.add(mEffect);
    }

    public void queueForRemoval(Creature creature, Effect effect) {
        ManagedEffect mEffect = new ManagedCreatureEffect(creature, effect);
        if (managedEffects.contains(mEffect))
            pendingRemovals.add(mEffect);
    }

    public void addCreatureEffect(Creature creature, Effect effect) {
        Set<Effect> effects = creatureEffects.get(creature);
        if (effects == null) {
            effects = new HashSet<Effect>();
            creatureEffects.put(creature, effects);
        }

        if (effect instanceof Periodic || effect instanceof Expirable) {
            manageEffect(creature, effect);
        }

        effects.add(effect);
        effect.apply(creature);
    }

    public void manualRemoveCreatureEffect(Creature creature, Effect effect) {
        if (effect == null)
            return;
        Set<Effect> effects = creatureEffects.get(creature);
        if (effects != null) {
            if (effect instanceof Expirable || effect instanceof Periodic) {
                queueForRemoval(creature, effect);
            }
            effects.remove(effect);
            if (effects.isEmpty()) {
                creatureEffects.remove(creature);
            }
        }
    }
    
    public void removeCreatureEffect(Creature creature, Effect effect) {
        if (effect == null)
            return;
        Set<Effect> effects = creatureEffects.get(creature);
        if (effects != null) {
            if (effect instanceof Expirable || effect instanceof Periodic) {
                queueForRemoval(creature, effect);
            }
            effect.remove(creature);
            effects.remove(effect);
            if (effects.isEmpty()) {
                creatureEffects.remove(creature);
            }
        }
    }

    public void clearCreatureEffects(Creature creature) {
        if (creatureEffects.containsKey(creature)) {
            Iterator<Effect> iter = creatureEffects.get(creature).iterator();
            while (iter.hasNext()) {
                Effect effect = iter.next();
                if (effect instanceof Expirable || effect instanceof Periodic) {
                    queueForRemoval(creature, effect);
                } else {
                    effect.remove(creature);
                    iter.remove();
                }
            }
            creatureEffects.remove(creature);
        }
    }

    public boolean creatureHasEffectType(Creature creature, EffectType type) {
        if (!creatureEffects.containsKey(creature))
            return false;
        
        for (Effect effect : creatureEffects.get(creature)) {
            if (effect.isType(type))
                return true;
        }
        
        return false;
    }
    
    public boolean creatureHasEffect(Creature creature, String name) {
        if (!creatureEffects.containsKey(creature))
            return false;

        for (Effect effect : creatureEffects.get(creature)) {
            if (effect.getName().equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    public Effect getCreatureEffect(Creature creature, String name) {
        Set<Effect> effects = creatureEffects.get(creature);
        if (effects == null)
            return null;

        for (Effect effect : effects) {
            if (effect.getName().equalsIgnoreCase(name))
                return effect;
        }

        return null;
    }

    public Set<Effect> getCreatureEffects(Creature creature) {
        if (!creatureEffects.containsKey(creature))
            return new HashSet<Effect>();

        return Collections.unmodifiableSet(creatureEffects.get(creature));
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
                            removeCreatureEffect(((ManagedCreatureEffect) managed).creature, managed.effect);
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
                            periodic.tick(((ManagedCreatureEffect) managed).creature);
                    }
                }
            }
            Heroes.debug.stopTask("EffectUpdater.run");
        }

    }

}
