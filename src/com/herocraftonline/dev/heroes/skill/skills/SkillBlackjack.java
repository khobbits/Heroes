package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillUseEvent;
import com.herocraftonline.dev.heroes.effects.Expirable;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.Periodic;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBlackjack extends ActiveSkill {

    private String applyText;
    private String expireText;
    private String stunApplyText;
    private String stunExpireText;

    private Random random = new Random();

    public SkillBlackjack(Heroes plugin) {
        super(plugin, "Blackjack");
        setDescription("Occasionally stuns your opponent");
        setUsage("/skill blackjack");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill blackjack", "skill bjack" });

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(this), Priority.Normal);
        registerEvent(Type.PLAYER_INTERACT, new SkillPlayerListener(), Priority.Normal);
        registerEvent(Type.CUSTOM_EVENT, new SkillUseListener(), Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% prepared his blackjack!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% sheathed his blackjack!");
        node.setProperty("stun-duration", 5000);
        node.setProperty("stun-chance", 0.20);
        node.setProperty("stun-apply-text", "%target% is stunned!");
        node.setProperty("stun-expire-text", "%target% is no longer stunned!");
        node.setProperty(Setting.DURATION.node(), 20000);
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% prepared his blackjack!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% sheathed his blackjack!").replace("%hero%", "$1");
        stunApplyText = getSetting(null, "stun-apply-text", "%target% is stunned!").replace("%target%", "$1");
        stunExpireText = getSetting(null, "stun-expire-text", "%target% is no longer stunned!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 20000);
        hero.addEffect(new BlackjackEffect(this, duration));

        return true;
    }

    public class BlackjackEffect extends ExpirableEffect {

        public BlackjackEffect(Skill skill, long duration) {
            super(skill, "Blackjack", duration);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

    }

    public class SkillEntityListener extends EntityListener {

        private final Skill skill;

        public SkillEntityListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getCause() != DamageCause.ENTITY_ATTACK || !(subEvent.getDamager() instanceof Player)) {
                    return;
                }

                Hero attackingHero = plugin.getHeroManager().getHero((Player) subEvent.getDamager());
                if (!attackingHero.hasEffect("Blackjack")) {
                    return;
                }
                Hero defendingHero = plugin.getHeroManager().getHero((Player) event.getEntity());

                double chance = getSetting(attackingHero.getHeroClass(), "stun-chance", 0.20);
                if (random.nextDouble() < chance) {
                    int duration = getSetting(attackingHero.getHeroClass(), "stun-duration", 5000);
                    defendingHero.addEffect(new StunEffect(skill, duration));
                }
            }
        }

    }

    public class SkillPlayerListener extends PlayerListener {

        @Override
        public void onPlayerInteract(PlayerInteractEvent event) {
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (hero.hasEffect("Stun")) {
                event.setCancelled(true);
            }
        }
    }
    
    public class SkillUseListener extends HeroesEventListener {
        
        @Override
        public void onSkillUse(SkillUseEvent event) {
            if (event.isCancelled())
                return;
            
            if (event.getHero().hasEffect("Stun")) {
                if (!(event.getSkill().getName().equals("Invuln")))
                    event.setCancelled(true);
            }
        }
    }

    public class StunEffect extends PeriodicEffect implements Periodic, Expirable {

        private static final long period = 100;

        private Location loc;

        public StunEffect(Skill skill, long duration) {
            super(skill, "Stun", period, duration);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);

            Player player = hero.getPlayer();
            loc = player.getLocation();
            broadcast(loc, stunApplyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);

            Player player = hero.getPlayer();
            broadcast(player.getLocation(), stunExpireText, player.getDisplayName());
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);

            Location location = hero.getPlayer().getLocation();
            if (location.getX() != loc.getX() || location.getY() != loc.getY() || location.getZ() != loc.getZ()) {
                loc.setYaw(location.getYaw());
                loc.setPitch(location.getPitch());
                hero.getPlayer().teleport(loc);
            }
        }
    }
}
