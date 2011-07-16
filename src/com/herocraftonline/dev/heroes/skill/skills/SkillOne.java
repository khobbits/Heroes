package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillOne extends ActiveSkill {
    
    private String applyText;
    private String expireText;

    public SkillOne(Heroes plugin) {
        super(plugin);
        setName("One");
        setDescription("Provides a short burst of speed");
        setUsage("/skill one");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill one");

        registerEvent(Type.PLAYER_MOVE, new SkillPlayerListener(), Priority.Normal);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty("speed", 0.7);
        node.setProperty("duration", 15000);
        node.setProperty("apply-text", "%hero% gained a burst of speed!");
        node.setProperty("expire-text", "%hero% returned to normal speed!");
        return node;
    }

    @Override
    public void init() {
        applyText = getSetting(null, "apply-text", "%hero% gained a burst of speed!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% returned to normal speed!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        int duration = getSetting(hero.getHeroClass(), "duration", 5000);
        hero.addEffect(new OneEffect(this, duration));

        return true;
    }

    public class OneEffect extends ExpirableEffect {

        public OneEffect(Skill skill, long duration) {
            super(skill, "One", duration);
        }

        @Override
        public void apply(Hero hero) {
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

    }

    public class SkillPlayerListener extends PlayerListener {

        @Override
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            Hero hero = plugin.getHeroManager().getHero(player);

            if (hero.hasEffect("One")) {
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
