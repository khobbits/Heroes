package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SkillShield extends PassiveSkill {

    public SkillShield(Heroes plugin) {
        super(plugin, "Shield");
        setDescription("Your shield absorbs damage!");
        setArgumentRange(0, 0);

        setTypes(SkillType.PHYSICAL);

        registerEvent(Type.CUSTOM_EVENT, new CustomListener(), Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("iron-door", 0.75);
        node.setProperty("wooden-door", 0.85);
        node.setProperty("trapdoor", 0.60);
        return node;
    }

    public class CustomListener extends CustomEventListener {

        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof WeaponDamageEvent))
                return;

            WeaponDamageEvent subEvent = (WeaponDamageEvent) event;
            if (subEvent.getCause() != DamageCause.ENTITY_ATTACK || subEvent.getDamage() == 0) {
                return;
            }
            if (subEvent.getEntity() instanceof Player) {
                Player player = (Player) subEvent.getEntity();
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    double multiplier = 1;
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
