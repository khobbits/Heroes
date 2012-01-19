package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillWolf extends PassiveSkill {

    public SkillWolf(Heroes plugin) {
        super(plugin, "Wolf");
        setDescription("You have the ability to tame wolves.");
        setUsage("/skill wolf <release|summon>");
        setArgumentRange(0, 1);
        setIdentifiers("skill wolf");
        setTypes(SkillType.SUMMON, SkillType.KNOWLEDGE);
        Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
    }


    public class SkillEntityListener implements Listener {

        private final SkillWolf skill;

        SkillEntityListener(SkillWolf skill) {
            this.skill = skill;
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEntityTame(EntityTameEvent event) {
            AnimalTamer owner = event.getOwner();
            Entity animal = event.getEntity();
            if (event.isCancelled() || !(animal instanceof Wolf) || !(owner instanceof Player)) {
                return;
            }

            Player player = (Player) owner;
            Hero hero = plugin.getHeroManager().getHero(player);
            if (!hero.canUseSkill(skill.getName())) {
                Messaging.send(player, "You can't tame wolves!");
                event.setCancelled(true);
            }
        }
    }


    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
