package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.CombustEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillFirearrow extends ActiveSkill {

    public SkillFirearrow(Heroes plugin) {
        super(plugin, "Firearrow");
        setDescription("Shoots a burning arrow");
        setUsage("/skill firearrow");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill firearrow", "skill farrow" });
        
        setTypes(SkillType.FIRE, SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.HARMFUL);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(this), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 4);
        node.setProperty("fire-ticks", 100);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        Arrow arrow = player.shootArrow();
        arrow.setFireTicks(1000);

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
            if (event.isCancelled())
                return;
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                Entity projectile = subEvent.getDamager();
                if (projectile instanceof Arrow) {
                    if (projectile.getFireTicks() > 0) {
                        Entity entity = subEvent.getEntity();
                        if (entity instanceof LivingEntity) {
                            Entity dmger = ((Arrow) subEvent.getDamager()).getShooter();
                            if (dmger instanceof Player) {
                                Hero hero = plugin.getHeroManager().getHero((Player) dmger);
                                HeroClass heroClass = hero.getHeroClass();
                                LivingEntity livingEntity = (LivingEntity) entity;
                                
                                if (!damageCheck((Player) dmger, livingEntity)) {
                                    return;
                                }
                                
                                // Damage the player and ignite them.
                                livingEntity.setFireTicks(getSetting(heroClass, "fire-ticks", 100));
                                if (livingEntity instanceof Player) {
                                    plugin.getHeroManager().getHero((Player) livingEntity).addEffect(new CombustEffect(skill, (Player) dmger));
                                } else if (livingEntity instanceof Creature) {
                                    plugin.getHeroManager().addCreatureEffect((Creature) livingEntity, new CombustEffect(skill, (Player) dmger));
                                }
                                int damage = getSetting(heroClass, Setting.DAMAGE.node(), 4);
                                addSpellTarget((Entity) entity, hero);
                                event.setDamage(damage);
                            }
                        }
                    }
                }
            }
        }
    }
}
