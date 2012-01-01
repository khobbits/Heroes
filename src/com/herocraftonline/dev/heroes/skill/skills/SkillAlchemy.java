package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.ContainerBrewingStand;
import net.minecraft.server.EntityPlayer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.getspout.spoutapi.event.inventory.InventoryListener;
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
        
        if (Heroes.useSpout) {
            registerEvent(Type.CUSTOM_EVENT, new SkillAlchemyListener(this), Priority.Lowest);
        }
    }
    
    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set(Setting.LEVEL.node(), 1);
        return section;
    }
    
    public class SkillAlchemyListener extends InventoryListener {
        
        private final Skill skill;
        public SkillAlchemyListener(Skill skill) {
            this.skill = skill;
        }
        @Override
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
}
