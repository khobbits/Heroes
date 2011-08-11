package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

public class SkillProwess extends PassiveSkill {

    public SkillProwess(Heroes plugin) {
        super(plugin, "Prowess");
        setDescription("You are more lethal with regular attacks!");
        setArgumentRange(0, 0);
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.Highest);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("attack-bonus", 1.25);
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
                    Hero hero = getPlugin().getHeroManager().getHero(player);
                    if (hero.hasEffect(getName())) {
                        subEvent.setDamage((int) (subEvent.getDamage() * getSetting(hero.getHeroClass(), "attack-bonus", 1.25)));
                    }
                } else if (subEvent.getDamager() instanceof Projectile) {
                    if (((Projectile) subEvent.getDamager()).getShooter() instanceof Player) {
                        Player player = (Player) ((Projectile)subEvent.getDamager()).getShooter();
                        Hero hero = getPlugin().getHeroManager().getHero(player);
                        if (hero.hasEffect(getName())) {
                            subEvent.setDamage((int) (subEvent.getDamage() * getSetting(hero.getHeroClass(), "attack-bonus", 1.25)));
                        }
                    }
                }
            }
        }
    }
}
