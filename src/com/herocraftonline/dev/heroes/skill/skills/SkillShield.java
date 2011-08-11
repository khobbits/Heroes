package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;

public class SkillShield extends PassiveSkill {

    public SkillShield(Heroes plugin) {
        super(plugin, "Shield");
        setDescription("Your shield absorbs damage!");
        setArgumentRange(0, 0);

        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.Highest);
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

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || event.getCause() != DamageCause.ENTITY_ATTACK || event.getDamage() == 0 || !(event instanceof EntityDamageByEntityEvent)) {
                return;
            }
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Hero hero = getPlugin().getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    double multiplier = 1;
                    if (player.getItemInHand().getType() == Material.IRON_DOOR) {
                        multiplier = getSetting(hero.getHeroClass(), "iron-door", 0.75);
                    } else if (player.getItemInHand().getType() == Material.WOODEN_DOOR) {
                        multiplier = getSetting(hero.getHeroClass(), "wooden-door", 0.85);
                    } else if (player.getItemInHand().getType() == Material.TRAP_DOOR) {
                        multiplier = getSetting(hero.getHeroClass(), "trapdoor", 0.60);
                    }
                    event.setDamage((int) (event.getDamage() * multiplier));
                }
            }
        }
    }
}
