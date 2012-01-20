package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SkillShield extends PassiveSkill {

    public SkillShield(Heroes plugin) {
        super(plugin, "Shield");
        setDescription("You are able to use doors as shields to absorbs damage!");
        setArgumentRange(0, 0);
        setEffectTypes(EffectType.BENEFICIAL, EffectType.PHYSICAL);
        setTypes(SkillType.PHYSICAL);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomListener(this), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("iron-door", 0.75);
        node.set("wooden-door", 0.85);
        node.set("trapdoor", 0.60);
        return node;
    }

    public class CustomListener implements Listener {

        private final Skill skill;
        
        public CustomListener(Skill skill) {
            this.skill = skill;
        }

        @EventHandler(priority = EventPriority.HIGHEST)
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
                    multiplier = SkillConfigManager.getUseSetting(hero, skill, "iron-door", 0.75, true);
                } else if (player.getItemInHand().getType() == Material.WOOD_DOOR) {
                    multiplier = SkillConfigManager.getUseSetting(hero, skill, "wooden-door", 0.85, true);
                } else if (player.getItemInHand().getType() == Material.TRAP_DOOR) {
                    multiplier = SkillConfigManager.getUseSetting(hero, skill, "trapdoor", 0.60, true);
                }
                event.setDamage((int) (event.getDamage() * multiplier));
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
