package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.Expirable;
import com.herocraftonline.dev.heroes.effects.Periodic;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillRoot extends TargettedSkill {

    private String applyText;
    private String expireText;

    private Listener rootRemover = new RootRemover();

    public SkillRoot(Heroes plugin) {
        super(plugin);
        setName("Root");
        setDescription("Roots your target in place");
        setUsage("/skill root <target>");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill root");

        registerEvent(Type.ENTITY_DEATH, rootRemover, Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("duration", 5000);
        node.setProperty("apply-text", "%target% was rooted!");
        node.setProperty("expire-text", "Root faded from %target%!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%target% was rooted!").replace("%target%", "$1");
        expireText = getSetting(null, "expire-text", "Root faded from %target%!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        Player targetPlayer = (Player) target;
        Hero targetHero = plugin.getHeroManager().getHero(targetPlayer);
        if (targetHero.equals(hero)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        broadcastExecuteText(hero, target);

        long duration = getSetting(hero.getHeroClass(), "duration", 5000);
        targetHero.addEffect(new RootEffect(this, duration));
        return true;
    }

    public class RootEffect extends PeriodicEffect implements Periodic, Expirable {

        private static final long period = 100;

        private double x, y, z;

        public RootEffect(Skill skill, long duration) {
            super(skill, "Root", period, duration);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);

            Location location = hero.getPlayer().getLocation();
            x = location.getX();
            y = location.getY();
            z = location.getZ();

            Player player = hero.getPlayer();
            broadcast(location, applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);

            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);

            Player player = hero.getPlayer();
            Location location = player.getLocation();
            if (location.getX() != x || location.getY() != y || location.getZ() != z) {
                location.setX(x);
                location.setY(y);
                location.setZ(z);
                player.teleport(location);
            }
        }
    }

    public class RootRemover extends EntityListener {

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            Entity entity = event.getEntity();
            if (entity instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) entity);
                Effect effect = hero.getEffect("Root");
                if (effect != null) {
                    hero.removeEffect(effect);
                }
            }
        }

    }
}
