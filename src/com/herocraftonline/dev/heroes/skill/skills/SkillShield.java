package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SkillShield extends PassiveSkill {

    public SkillShield(Heroes plugin) {
        super(plugin, "Shield");
        setDescription("Your shield absorbs damage!");
        setArgumentRange(0, 0);
        setEffectTypes(EffectType.BENEFICIAL, EffectType.PHYSICAL);
        setTypes(SkillType.PHYSICAL);

        registerEvent(Type.CUSTOM_EVENT, new CustomListener(), Priority.Highest);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("iron-door", 0.75);
        node.set("wooden-door", 0.85);
        node.set("trapdoor", 0.60);
        return node;
    }

    public class CustomListener extends HeroesEventListener {


        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.getCause() != DamageCause.ENTITY_ATTACK || event.getDamage() == 0 || !(event.getEntity() instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.hasEffect(getName())) {
                double multiplier = 1;
                if (player.getItemInHand().getType() == Material.IRON_DOOR) {
                    multiplier = getSetting(hero, "iron-door", 0.75, true);
                } else if (player.getItemInHand().getType() == Material.WOOD_DOOR) {
                    multiplier = getSetting(hero, "wooden-door", 0.85, true);
                } else if (player.getItemInHand().getType() == Material.TRAP_DOOR) {
                    multiplier = getSetting(hero, "trapdoor", 0.60, true);
                }
                event.setDamage((int) (event.getDamage() * multiplier));
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
