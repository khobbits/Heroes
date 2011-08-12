package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillSneak extends ActiveSkill {
    
    private String applyText;
    private String expireText;
    
    public SkillSneak(Heroes plugin) {
        super(plugin, "Sneak");
        setDescription("You crouch into the shadows");
        setUsage("/skill stealth");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill stealth"});
        
        registerEvent(Type.PLAYER_TOGGLE_SNEAK, new SneakListener(), Priority.Highest);
        if (getSetting(null, "damage-cancels", true) == true) {
            registerEvent(Type.ENTITY_DAMAGE, new SneakDamageListener(), Priority.Monitor);
        }
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("duration", 600000); // 10 minutes in milliseconds
        node.setProperty("apply-text", "%hero% faded into the shadows!");
        node.setProperty("expire-text", "%hero% reappeared!");
        node.setProperty("damage-cancels", true);
        node.setProperty("refresh-interval", 5000); // in milliseconds
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%hero% faded into the shadows!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% reappeard!").replace("%hero%", "$1");
        
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = getSetting(hero.getHeroClass(), "duration", 600000);
        int period = getSetting(hero.getHeroClass(), "refresh-interval", 5000);
        hero.addEffect(new SneakEffect(this, period, duration));

        return true;
    }
    
    public class SneakEffect extends PeriodicEffect {

        public SneakEffect(Skill skill, long period, long duration) {
            super(skill, "Sneak", period, duration);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            player.setSneaking(true);
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            player.setSneaking(false);
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }
        
        @Override
        public void tick(Hero hero) {
            super.tick(hero);
            hero.getPlayer().setSneaking(false);
            hero.getPlayer().setSneaking(true);
        }
    }
    
    public class SneakListener extends PlayerListener {
        
        @Override
        public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
            Hero hero = getPlugin().getHeroManager().getHero(event.getPlayer());
            if (hero.hasEffect("Sneak")) {
                event.getPlayer().setSneaking(true);
                event.setCancelled(true);
            }
        }
    }
    
    public class SneakDamageListener extends EntityListener {
        
        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) return;
            Player player = (Player) event.getEntity();
            Hero hero = getPlugin().getHeroManager().getHero(player);
            if (hero.hasEffect("Sneak")) {
                player.setSneaking(false);
                hero.removeEffect(hero.getEffect("Sneak"));
            }
        }
    }
}
