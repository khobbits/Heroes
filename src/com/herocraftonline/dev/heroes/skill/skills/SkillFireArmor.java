package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillFireArmor extends PassiveSkill {

    private String igniteText;
    private List<String> defaultArmors = new ArrayList<String>();

    public SkillFireArmor(Heroes plugin) {
        super(plugin, "FireArmor");
        setDescription("Gives your armor a chance to ignite your attackers!");
        setTypes(SkillType.FIRE, SkillType.SILENCABLE, SkillType.BUFF);
        setEffectTypes(EffectType.FIRE);
        defaultArmors.add(Material.GOLD_CHESTPLATE.name());

        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(), Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("armors", defaultArmors);
        node.setProperty("ignite-chance", 0.20);
        node.setProperty("ignite-duration", 5000);
        node.setProperty("ignite-text", "%hero% ignited %target% with firearmor!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        igniteText = getSetting(null, "ignite-text", "%hero% ignited %target% with firearmor!").replace("%hero%", "$1").replace("%target%", "$2");
    }

    public class SkillDamageListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event instanceof EntityDamageByEntityEvent)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getHeroManager().getHero(player);

            if (!hero.hasEffect("FireArmor") || !getSetting(hero, "armors", defaultArmors).contains(player.getInventory().getChestplate().getType().name())) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            // Dont set Projectiles on fire
            if (!(subEvent.getDamager() instanceof LivingEntity)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            // Check our ignite chance
            double chance = getSetting(hero, "ignite-chance", .2, false);
            if (Util.rand.nextDouble() >= chance) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            // Set the damager on fire if it was successful
            int fireTicks = getSetting(hero, "ignite-duration", 5000, false) / 50;
            subEvent.getDamager().setFireTicks(fireTicks);

            String name = null;
            if (subEvent.getDamager() instanceof Player) {
                name = ((Player) subEvent.getDamager()).getName();
            } else if (subEvent.getDamager() instanceof Creature) {
                Messaging.getCreatureName((Creature) subEvent.getDamager());
            }

            broadcast(player.getLocation(), igniteText, player.getDisplayName(), name);
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
