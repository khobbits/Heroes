package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillBackstab extends PassiveSkill {

    private String useText;
    
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
        node.setProperty(Setting.USE_TEXT.node(), "%hero% backstabbed %target%!");
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        useText = getSetting(null, Setting.USE_TEXT.node(), "%hero% backstabbed %target%!").replace("%hero%", "$1").replace("%target%", "$2");
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
                ItemStack item = player.getItemInHand();
                
                if (!getSetting(hero, "weapons", Util.swords).contains(item.getType().name())) {
                    Heroes.debug.stopTask("HeroesSkillListener");
                    return;
                }
                
                if (event.getEntity().getLocation().getDirection().dot(player.getLocation().getDirection()) <= 0) {
                    Heroes.debug.stopTask("HeroesSkillListener");
                    return;
                }

                if (hero.hasEffect("Sneak") && Util.rand.nextDouble() < getSetting(hero, "sneak-chance", 1.0, false)) {
                    event.setDamage((int) (event.getDamage() * getSetting(hero, "sneak-bonus", 2.0, false)));
                } else if (Util.rand.nextDouble() < getSetting(hero, "attack-chance", .5, false)) {
                    event.setDamage((int) (event.getDamage() * getSetting(hero, "attack-bonus", 1.5, false)));
                }

                Entity target = event.getEntity();
                broadcastExecuteText(hero, target);
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
    
    private void broadcastExecuteText(Hero hero, Entity target) {
        Player player = hero.getPlayer();
        String targetName = target instanceof Player ? ((Player) target).getName() : target.getClass().getSimpleName().substring(5);
        broadcast(player.getLocation(), useText, player.getDisplayName(), target == player ? "himself" : targetName);
    }
}
