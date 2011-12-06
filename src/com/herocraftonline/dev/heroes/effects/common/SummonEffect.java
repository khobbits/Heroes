package com.herocraftonline.dev.heroes.effects.common;

import net.minecraft.server.EntityCreature;

import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

    public class SummonEffect extends ExpirableEffect {

        private Hero summoner;
        private final String expireText;
        
        public SummonEffect(Skill skill, long duration, Hero summoner, String expireText) {
            super(skill, "Summon", duration);
            this.summoner = summoner;
            this.expireText = expireText;
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.BENEFICIAL);
        }

        @Override
        public void apply(LivingEntity lEntity) {
            super.apply(lEntity);
            summoner.getSummons().add(lEntity);
            FollowEffect fEffect = new FollowEffect(skill, 1500);
            summoner.addEffect(fEffect);
        }

        @Override
        public void remove(LivingEntity creature) {
            super.remove(creature);
            summoner.getSummons().remove(creature);
            broadcast(creature.getLocation(), expireText);
            //Load the chunk first
            if (!creature.getWorld().getChunkAt(creature.getLocation()).isLoaded()) 
                creature.getWorld().loadChunk(creature.getWorld().getChunkAt(creature.getLocation()));
            creature.remove();

            // Check if the summoner has anymore creatures with Summon
            for (LivingEntity le : summoner.getSummons()) {
                if (plugin.getEffectManager().entityHasEffect(le, name))
                    return;
            }
            // If there are no more summoned skeletons lets remove the follow effect
            summoner.removeEffect(summoner.getEffect("Follow"));
        }
        
        public Hero getSummoner() {
            return summoner;
        }
        
        public class FollowEffect extends PeriodicEffect {

            public FollowEffect(Skill skill, long period) {
                super(skill, "Follow", period);
                this.setPersistent(true);
            }

            @Override
            public void apply(Hero hero) {
                super.apply(hero);
            }

            @Override
            public void remove(Hero hero) {
                super.remove(hero);
            }

            @Override
            public void tick(Hero hero) {
                super.tick(hero);
                for (LivingEntity le : hero.getSummons()) {
                    if (!(le instanceof Creature))
                        continue;
                    
                    Creature creature = (Creature) le;
                        
                    if (plugin.getEffectManager().entityHasEffect(creature, "Summon")) {
                        if (creature.getTarget() != null && creature.getTarget() instanceof LivingEntity)
                            continue;

                        follow(creature, hero);
                    }
                }
            }

            /**
             * Moves the creature toward the player
             * 
             * @param creature
             * @param hero
             */
            private void follow(Creature creature, Hero hero) {
                // If the creature is far away lets teleport them to the player
                if (creature.getLocation().distanceSquared(hero.getPlayer().getLocation()) > 400) {
                    creature.teleport(hero.getPlayer());
                }

                // Initiate pathing to the player
                EntityCreature cEntity = ((CraftCreature) creature).getHandle();
                cEntity.pathEntity = cEntity.world.findPath(cEntity, ((CraftPlayer) hero.getPlayer()).getHandle(), 16.0F);
            }
        }
    }