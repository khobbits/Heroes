package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSneak extends ActiveSkill {

    private boolean damageCancels;
    private boolean attackCancels;

    public SkillSneak(Heroes plugin) {
        super(plugin, "Sneak");
        setDescription("You crouch into the shadows");
        setUsage("/skill stealth");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill sneak" });

        registerEvent(Type.PLAYER_TOGGLE_SNEAK, new SneakListener(), Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 600000); // 10 minutes in milliseconds
        node.setProperty("damage-cancels", true);
        node.setProperty("atacking-cancels", true);
        node.setProperty("refresh-interval", 5000); // in milliseconds
        return node;
    }

    @Override
    public void init() {
        super.init();
        damageCancels = getSetting(null, "damage-cancels", true);
        attackCancels = getSetting(null, "attacking-cancels", true);
        if (damageCancels || attackCancels) {
            registerEvent(Type.ENTITY_DAMAGE, new SneakDamageListener(), Priority.Monitor);
        }
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        if (hero.hasEffect("Sneak")) {
            hero.removeEffect(hero.getEffect("Sneak"));
        } else {
            Messaging.send(hero.getPlayer(), "You are now sneaking");

            int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 600000);
            int period = getSetting(hero.getHeroClass(), "refresh-interval", 5000);
            hero.addEffect(new SneakEffect(this, period, duration));

        }
        return true;
    }

    public class SneakEffect extends PeriodicEffect {

        public SneakEffect(Skill skill, long period, long duration) {
            super(skill, "Sneak", period, duration);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.PHYSICAL);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            player.setSneaking(true);
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            player.setSneaking(false);
            Messaging.send(player, "You are no longer sneaking!");
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
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (hero.hasEffect("Sneak")) {
                event.getPlayer().setSneaking(true);
                event.setCancelled(true);
            }
        }
    }

    public class SneakDamageListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !damageCancels)
                return;
            Player player = null;
            if (event.getEntity() instanceof Player) {
                player = (Player) event.getEntity();
            } else if (attackCancels && event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getDamager() instanceof Player) {
                    player = (Player) subEvent.getDamager();
                } else if (subEvent.getDamager() instanceof Projectile) {
                    if (((Projectile) subEvent.getDamager()).getShooter() instanceof Player) {
                        player = (Player) ((Projectile) subEvent.getDamager()).getShooter();
                    }
                }
            }
            if (player == null)
                return;

            Hero hero = plugin.getHeroManager().getHero(player);
            if (hero.hasEffect("Sneak")) {
                player.setSneaking(false);
                hero.removeEffect(hero.getEffect("Sneak"));
            }
        }
    }
}
