package com.herocraftonline.dev.heroes.command.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class SkillStar extends PassiveSkill {

    public SkillStar(Heroes plugin) {
        super(plugin);
        name = "Star";
        description = "Throw snowballs for damage";
        minArgs = 0;
        maxArgs = 0;

        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.High);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 4);
        return node;
    }

    public class SkillPlayerListener extends EntityListener {

        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event.getCause() == DamageCause.ENTITY_ATTACK)) {
                return;
            }
            if (event instanceof EntityDamageByProjectileEvent) {
                EntityDamageByProjectileEvent subEvent = (EntityDamageByProjectileEvent) event;
                if (subEvent.getEntity() instanceof Player) {
                    Player player = (Player) subEvent.getEntity();
                    Hero hero = plugin.getHeroManager().getHero(player);
                    if (hero.getEffects().hasEffect(name)) {
                        int damage = getSetting(hero.getHeroClass(), "damage", 4);
                        subEvent.setDamage(damage);
                    }
                }
            }
        }
    }

}
