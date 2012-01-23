package com.herocraftonline.dev.heroes.skill.skills;

import java.util.logging.Level;

import net.minecraft.server.ContainerBrewingStand;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillAlchemy extends PassiveSkill {

    public SkillAlchemy(Heroes plugin) {
        super(plugin, "Alchemy");
        setDescription("You are able to craft potions!");
        setArgumentRange(0, 0);
        setTypes(SkillType.KNOWLEDGE, SkillType.ITEM);
        setEffectTypes(EffectType.BENEFICIAL);
        
        if (Heroes.useSpout()) {
            Bukkit.getServer().getPluginManager().registerEvents(new SkillSpoutListener(this), plugin);
        }else {
            Heroes.log(Level.WARNING, "SkillAlchemy requires Spout! Remove from your skills directory if you will not use!");
        }
    }
    
    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set(Setting.LEVEL.node(), 1);
        return section;
    }
    
    public class SkillSpoutListener implements Listener {
        
        private final Skill skill;
        public SkillSpoutListener(Skill skill) {
            this.skill = skill;
        }
        
        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryOpen(InventoryOpenEvent event) {
            if (event.isCancelled())
                return;
            
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            EntityPlayer eP = ((CraftPlayer) event.getPlayer()).getHandle();
            if (!(eP.activeContainer instanceof ContainerBrewingStand))
                return;
            
            if (!hero.canUseSkill(skill)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
