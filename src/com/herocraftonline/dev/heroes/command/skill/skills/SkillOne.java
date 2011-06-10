package com.herocraftonline.dev.heroes.command.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.ActiveEffectSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.persistence.HeroEffects;

public class SkillOne extends ActiveEffectSkill {

    public SkillOne(Heroes plugin) {
        super(plugin);
        name = "One";
        description = "Provides a short burst of speed";
        usage = "/skill one";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill one");

        registerEvent(Type.PLAYER_MOVE, new SkillPlayerListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("speed", 0.7);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        applyEffect(hero);
        notifyNearbyPlayers(hero.getPlayer().getLocation(), useText, hero.getPlayer().getName(), name);
        return true;
    }

    public class SkillPlayerListener extends PlayerListener {

        @Override
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            Hero hero = plugin.getHeroManager().getHero(player);

            HeroEffects effects = hero.getEffects();
            if (effects.hasEffect(name)) {
                double speed = getSetting(hero.getHeroClass(), "speed", 0.7);
                Location loc = player.getLocation();
                Vector dir = loc.getDirection().normalize();
                dir.setX(dir.getX() * speed);
                dir.setZ(dir.getZ() * speed);
                dir.setY(0);
                if (player.getWorld().getBlockTypeIdAt(loc.getBlockX(), loc.getBlockY() - 2, loc.getBlockZ()) == 0) {
                    dir.setY(-0.5);
                }
                player.setVelocity(dir);
            }
        }

    }
}
