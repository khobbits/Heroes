package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillWeb extends TargettedSkill {

    private String applyText;
    private Set<Location> protectedWebs = new HashSet<Location>();
    
    public SkillWeb(Heroes plugin) {
        super(plugin, "Web");
        setDescription("Catches your target in a web");
        setUsage("/skill web [target]");
        setArgumentRange(0, 1);
        setIdentifiers(new String[]{"skill web"});
        
        registerEvent(Type.BLOCK_BREAK, new WebBlockListener(), Priority.Highest);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("range", 10);
        node.setProperty("duration", 5000); //in milliseconds
        node.setProperty("apply-text", "%hero% conjured a web at %target%'s feet!");
        return node;
    }
    
    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (target.equals(player))  return false;
        String name = "";
        
        if (target instanceof Player) {
            name = ((Player) target).getDisplayName();
        } else if (target instanceof Creature) {
            name = Messaging.getCreatureName((Creature) target).toLowerCase();
        }
        target.getLocation().getBlock().setType(Material.WEB);
        protectedWebs.add(target.getLocation());
        broadcast(player.getLocation(), applyText, new Object[] {player.getDisplayName(), name});
        return false;
    }

    public class WebEffect extends ExpirableEffect {

        private Location location;
        
        public WebEffect(Skill skill, long duration, Location location) {
            super(skill, "Web", duration);
            this.location = location;
        }
        
        @Override
        public void apply(Hero hero) {
            super.apply(hero);
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            location.getBlock().setType(Material.AIR);
            protectedWebs.remove(location);
        }
        
        public Location getLocation() {
            return this.location;
        }
    }
    
    public class WebBlockListener extends BlockListener {
        
        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            if (event.isCancelled()) return;
            
            if (protectedWebs.contains(event.getBlock().getLocation())) event.setCancelled(true);
        }
    }
}
