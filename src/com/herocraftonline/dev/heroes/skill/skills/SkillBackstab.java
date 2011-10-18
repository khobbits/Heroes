package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;
import com.herocraftonline.util.ConfigurationNode;

public class SkillBackstab extends PassiveSkill {

    public SkillBackstab(Heroes plugin) {
        super(plugin, "Backstab");
        setDescription("You are more lethal when attacking from behind!");
        setArgumentRange(0, 0);
        setTypes(SkillType.PHYSICAL, SkillType.BUFF);
        setEffectTypes(EffectType.BENEFICIAL, EffectType.PHYSICAL);
        
        registerEvent(Type.CUSTOM_EVENT, new SkillHeroesListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("weapons", Util.swords);
        node.setProperty("attack-bonus", 1.5);
        node.setProperty("attack-chance", .5);
        node.setProperty("sneak-bonus", 2.0); // Alternative bonus if player is sneaking when doing the backstab
        node.setProperty("sneak-chance", 1.0);
        return node;
    }

    public class SkillHeroesListener extends HeroesEventListener {

        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (!(event.getDamager() instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            Player player = (Player) event.getDamager();
            Hero hero = plugin.getHeroManager().getHero(player);
            
            if (hero.hasEffect(getName())) {
                HeroClass heroClass = hero.getHeroClass();
                ItemStack item = player.getItemInHand();
                
                if (!getSetting(heroClass, "weapons", Util.swords).contains(item.getType().name())) {
                    Heroes.debug.stopTask("HeroesSkillListener");
                    return;
                }
                
                if (event.getEntity().getLocation().getDirection().dot(player.getLocation().getDirection()) <= 0) {
                    Heroes.debug.stopTask("HeroesSkillListener");
                    return;
                }

                if (hero.hasEffect("Sneak") && Util.rand.nextDouble() < getSetting(heroClass, "sneak-chance", 1.0)) {
                    event.setDamage((int) (event.getDamage() * getSetting(heroClass, "sneak-bonus", 2.0)));
                } else if (Util.rand.nextDouble() < getSetting(heroClass, "attack-chance", .5)) {
                    event.setDamage((int) (event.getDamage() * getSetting(heroClass, "attack-bonus", 1.5)));
                }

                String name = "";
                Entity target = event.getEntity();

                if (target instanceof Player) {
                    name = ((Player) target).getName();
                    Messaging.send((Player) target, player.getName() + " has backstabbed you!");
                } else if (target instanceof Creature) {
                    name = "a " + Messaging.getCreatureName((Creature) target).toLowerCase();
                }

                Messaging.send(player, "You have backstabbed " + name + "!");
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
