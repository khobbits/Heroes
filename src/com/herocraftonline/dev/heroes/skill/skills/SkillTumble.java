package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SkillTumble extends PassiveSkill {

    public SkillTumble(Heroes plugin) {
        super(plugin, "Tumble");
        setDescription("You are able to fall from higher distances without taking damage!");
        setEffectTypes(EffectType.BENEFICIAL, EffectType.PHYSICAL);
        setTypes(SkillType.PHYSICAL, SkillType.BUFF);
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Low);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("distance-per-level", .5);
        node.setProperty("base-distance", 3);
        return node;
    }
    
    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (!(event.getEntity() instanceof Player) || event.getCause() != DamageCause.FALL) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            Hero hero = plugin.getHeroManager().getHero((Player) event.getEntity());
            if (!hero.hasEffect("Tumble")) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            int distance = (int) (getSetting(hero, "base-distance", 3, false) + (hero.getLevel() * getSetting(hero, "distance-per-level", .5, false)));
            int fallDistance = (event.getDamage() - 3) * 3;
            fallDistance -= distance;
            if (fallDistance <= 0)
                event.setCancelled(true);
            else 
                event.setDamage(3 + (fallDistance / 3));
            
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
