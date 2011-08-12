package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesWeaponDamageEvent;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;

public class SkillProwess extends PassiveSkill {

    public SkillProwess(Heroes plugin) {
        super(plugin, "Prowess");
        setDescription("You are more lethal with regular attacks!");
        setArgumentRange(0, 0);

        registerEvent(Type.CUSTOM_EVENT, new CustomListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("attack-bonus", 1.25);
        return node;
    }

    public class CustomListener extends CustomEventListener {

        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof HeroesWeaponDamageEvent)) return;
            HeroesWeaponDamageEvent subEvent = (HeroesWeaponDamageEvent) event;

            if (subEvent.getCause() != DamageCause.ENTITY_ATTACK)  return;
            
            if (subEvent.getDamager() instanceof Player) {
                Player player = (Player) subEvent.getDamager();
                Hero hero = getPlugin().getHeroManager().getHero(player);
                double damageBonus = getSetting(hero.getHeroClass(), "attack-bonus", 1.25);
                
                if (hero.hasEffect(getName())) {
                    subEvent.setDamage((int) (subEvent.getDamage() * damageBonus));
                }
            } else if (subEvent.getDamager() instanceof Projectile) {
                if (((Projectile) subEvent.getDamager()).getShooter() instanceof Player) {
                    Player player = (Player) ((Projectile)subEvent.getDamager()).getShooter();
                    Hero hero = getPlugin().getHeroManager().getHero(player);
                    double damageBonus = getSetting(hero.getHeroClass(), "attack-bonus", 1.25);
                    
                    if (hero.hasEffect(getName())) {
                        subEvent.setDamage((int) (subEvent.getDamage() * damageBonus));
                    }
                }
            }
        }
    }
}
    