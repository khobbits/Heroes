package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;

public class SkillSwordMastery extends PassiveSkill {

    public SkillSwordMastery(Heroes plugin) {
        super(plugin);
        setName("SwordMastery");
        setDescription("Scales your damage with a gold sword depending on your level");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill swordmastery");

        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.Normal);
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
                    if (hero.hasEffect(getName())) {
                        if (player.getItemInHand().getType() == Material.GOLD_SWORD) {
                            event.setDamage(event.getDamage() + Math.round(hero.getLevel() / 20));
                        }
                    }
                }
            }
        }

    }
}
