package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;

public class SkillShield extends PassiveSkill {

    public SkillShield(Heroes plugin) {
        super(plugin);
        setName("Shield");
        setDescription("Your shield absorbs damage!");
        setMinArgs(0);
        setMaxArgs(0);

        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.High);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("iron-door", 0.75);
        node.setProperty("wooden-door", 0.85);
        node.setProperty("trapdoor", 0.60);
        return node;
    }

    public class SkillPlayerListener extends EntityListener {

        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event.getCause() == DamageCause.ENTITY_ATTACK)) {
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getEntity() instanceof Player) {
                    Player player = (Player) subEvent.getEntity();
                    Hero hero = plugin.getHeroManager().getHero(player);
                    if (hero.hasEffect(getName())) {
                        double multiplier = 0;
                        if (player.getItemInHand().getType() == Material.IRON_DOOR) {
                            multiplier = getSetting(hero.getHeroClass(), "iron-door", 0.75);

                        } else if (player.getItemInHand().getType() == Material.WOODEN_DOOR) {
                            multiplier = getSetting(hero.getHeroClass(), "wooden-door", 0.85);
                        } else if (player.getItemInHand().getType() == Material.TRAP_DOOR) {
                            multiplier = getSetting(hero.getHeroClass(), "trapdoor", 0.60);
                        }
                        subEvent.setDamage((int) (subEvent.getDamage() * multiplier));
                    }
                }
            }
        }
    }
}
