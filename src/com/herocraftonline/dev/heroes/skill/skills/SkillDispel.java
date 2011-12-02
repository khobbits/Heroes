package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillDispel extends TargettedSkill {

    public SkillDispel(Heroes plugin) {
        super(plugin, "Dispel");
        setDescription("Removes all magical effects from your target");
        setUsage("/skill dispel");
        setArgumentRange(0, 1);
        setIdentifiers("skill dispel");
        setTypes(SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("max-removals", 3);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        boolean removed = false;
        int maxRemovals = getSetting(hero, "max-removals", 3, false);
        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            // if player is targetting itself
            if (targetPlayer.equals(player)) {
                for (Effect effect : hero.getEffects()) {
                    if (effect.isType(EffectType.DISPELLABLE) && effect.isType(EffectType.HARMFUL)) {
                        hero.removeEffect(effect);
                        removed = true;
                        maxRemovals--;
                        if (maxRemovals == 0) {
                            break;
                        }
                    }
                }
            } else {
                Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
                boolean removeHarmful = false;
                if (hero.hasParty()) {
                    // If the target is a partymember lets make sure we only remove harmful effects
                    if (hero.getParty().isPartyMember(targetHero)) {
                        removeHarmful = true;
                    }
                }
                for (Effect effect : targetHero.getEffects()) {
                    if (effect.isType(EffectType.DISPELLABLE)) {
                        if (removeHarmful && effect.isType(EffectType.HARMFUL)) {
                            targetHero.removeEffect(effect);
                            removed = true;
                            maxRemovals--;
                            if (maxRemovals == 0) {
                                break;
                            }
                        } else if (!removeHarmful && effect.isType(EffectType.BENEFICIAL)) {
                            targetHero.removeEffect(effect);
                            removed = true;
                            maxRemovals--;
                            if (maxRemovals == 0) {
                                break;
                            }
                        }
                    }
                }
            }
        } else if (target instanceof Creature) {
            Set<Effect> cEffects = plugin.getEffectManager().getCreatureEffects((Creature) target);
            if (cEffects != null) {
                boolean removeHarmful = false;
                if (hero.getSummons().contains(target)) {
                    removeHarmful = true;
                }
                for (Effect effect : cEffects) {
                    if (effect.isType(EffectType.DISPELLABLE)) {
                        if (removeHarmful && effect.isType(EffectType.HARMFUL)) {
                            plugin.getEffectManager().removeCreatureEffect((Creature) target, effect);
                            removed = true;
                            maxRemovals--;
                            if (maxRemovals == 0) {
                                break;
                            }
                        } else if (!removeHarmful && effect.isType(EffectType.BENEFICIAL)) {
                            plugin.getEffectManager().removeCreatureEffect((Creature) target, effect);
                            removed = true;
                            maxRemovals--;
                            if (maxRemovals == 0) {
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            return SkillResult.INVALID_TARGET;
        }

        if (removed) {
            broadcastExecuteText(hero, target);
            return SkillResult.NORMAL;
        }
        Messaging.send(player, "The target has nothing to dispel!");
        return SkillResult.INVALID_TARGET_NO_MSG;
    }

}
