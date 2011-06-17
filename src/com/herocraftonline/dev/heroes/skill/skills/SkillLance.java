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

public class SkillLance extends PassiveSkill {

    private static final int baseDamage = 2;

    public SkillLance(Heroes plugin) {
        super(plugin);
        name = "Lance";
        description = "Increases your shovel damage (passive)";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("skill lance");

        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage-multipler", 2d);
        return node;
    }

    public class SkillPlayerListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event.getCause() == DamageCause.ENTITY_ATTACK)) {
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getDamager() instanceof Player) {
                    Player player = (Player) subEvent.getDamager();
                    Hero hero = plugin.getHeroManager().getHero(player);
                    if (hero.getEffects().hasEffect(name)) {
                        if (player.getItemInHand().getType().toString().contains("SHOVEL")) {
                            double multiplier = getSetting(hero.getHeroClass(), "damage-multiplier", 2d);
                            event.setDamage((int) (baseDamage * multiplier));
                        }
                    }
                }
            }
        }

    }
}
