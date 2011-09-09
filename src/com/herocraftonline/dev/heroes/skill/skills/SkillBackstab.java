package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class SkillBackstab extends PassiveSkill {

    private Random rand = new Random();
    
    public SkillBackstab(Heroes plugin) {
        super(plugin, "Backstab");
        setDescription("You are more lethal when attacking from behind!");
        setArgumentRange(0, 0);
        
        setTypes(SkillType.PHYSICAL, SkillType.BUFF);
        
        registerEvent(Type.CUSTOM_EVENT, new CustomListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("weapons", Properties.defaultWeapons);
        node.setProperty("attack-bonus", 1.5);
        node.setProperty("attack-chance", .5);
        node.setProperty("sneak-bonus", 2.0); // Alternative bonus if player is sneaking when doing the backstab
        node.setProperty("sneak-chance", 1.0);
        return node;
    }

    public class CustomListener extends CustomEventListener {

        @Override
        public void onCustomEvent(Event event) {
            if (!(event instanceof WeaponDamageEvent))
                return;

            WeaponDamageEvent subEvent = (WeaponDamageEvent) event;
            if (subEvent.getDamager() instanceof Player) {
                Player player = (Player) subEvent.getDamager();
                ItemStack item = player.getItemInHand();
                Hero hero = plugin.getHeroManager().getHero(player);
                if (!getSetting(hero.getHeroClass(), "weapons", Properties.defaultWeapons).contains(item.getType().name()))
                    return;
                if (hero.hasEffect(getName())) {
                    if (subEvent.getEntity().getLocation().getDirection().dot(player.getLocation().getDirection()) <= 0)
                        return;

                    if (hero.hasEffect("Sneak") && rand.nextDouble() < getSetting(hero.getHeroClass(), "sneak-chance", 1.0)) {
                        subEvent.setDamage((int) (subEvent.getDamage() * getSetting(hero.getHeroClass(), "sneak-bonus", 2.0)));
                    } else if (rand.nextDouble() < getSetting(hero.getHeroClass(), "attack-chance", .5)) {
                        subEvent.setDamage((int) (subEvent.getDamage() * getSetting(hero.getHeroClass(), "attack-bonus", 1.5)));
                    }
                    String name = "";
                    if (subEvent.getEntity() instanceof Player) {
                        name = ((Player) subEvent.getEntity()).getName();
                        Messaging.send((Player) subEvent.getEntity(), player.getName() + " has backstabbed you!");
                    } else if (subEvent.getEntity() instanceof Creature) {
                        name = "a " + Messaging.getCreatureName((Creature) subEvent.getEntity()).toLowerCase();
                    }

                    Messaging.send(player, "You have backstabbed " + name);
                }
            }
        }
    }
}
