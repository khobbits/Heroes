package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

public class SkillIronBones extends PassiveSkill {

    public SkillIronBones(Heroes plugin) {
        super(plugin, "IronBones");
        setDescription("You hit the ground with a thunderous roar!");
        setArgumentRange(0, 0);
        setEffectTypes( EffectType.PHYSICAL);
        setTypes(SkillType.PHYSICAL);

        registerEvent(Event.Type.ENTITY_DAMAGE, new DamageListener(), Event.Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage", 0.10);
        node.setProperty("radius", 10);
        return node;
    }

    public class DamageListener extends EntityListener {

        public void onEntityDamage(EntityDamageEvent e) {
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                return;
            }

            if (!(e.getEntity() instanceof Player)) {
                return;
            }
            Player player = (Player) e.getEntity();
            Hero hero = plugin.getHeroManager().getHero();

            if (!hero.hasEffect(getName())) {
                return;
            }

            double damage = e.getDamage() * getSetting(hero.getHeroClass(), "damage", 0.10);
            int radius = getSetting(hero.getHeroClass(), "radius", 10);

            for (Entity n : player.getNearbyEntities(radius, radius, radius)) {
                if (n instanceof LivingEntity) {
                    ((LivingEntity) n).damage((int) damage);
                }
            }


        }


    }


}
