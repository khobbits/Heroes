package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillBlackjack extends ActiveSkill {

    private String applyText;
    private String expireText;

    private PlayerListener playerListener = new SkillPlayerListener();
    private EntityListener entityListener = new SkillEntityListener();

    private Map<Integer, Long> stunnedEntities = new HashMap<Integer, Long>();
    private Random random = new Random();

    public SkillBlackjack(Heroes plugin) {
        super(plugin);
        setName("Blackjack");
        setDescription("Gives your melee attacks a chance to stun");
        setUsage("/skill blackjack");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill blackjack");

        registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Normal);
        registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Normal);
        registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Highest);
        registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty("apply-text", "%hero% prepared his blackjack!");
        node.setProperty("expire-text", "%hero% sheathed his blackjack!");
        node.setProperty("stun-duration", 5000);
        node.setProperty("stun-chance", 0.20);
        node.setProperty("duration", 20000);
        return node;
    }

    @Override
    public void init() {
        applyText = getSetting(null, "apply-text", "%hero% prepared his blackjack!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% sheathed his blackjack!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        int duration = getSetting(hero.getHeroClass(), "duration", 5000);
        hero.addEffect(new BlackjackEffect(this, duration));
        
        return true;
    }

    private boolean checkStunned(Entity entity) {
        if (entity == null) {
            return false;
        }
        int id = entity.getEntityId();
        if (stunnedEntities.containsKey(id)) {
            if (stunnedEntities.get(id) > System.currentTimeMillis()) {
                return true;
            } else {
                stunnedEntities.remove(id);
                return false;
            }
        }
        return false;
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

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled()) {
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getCause() == DamageCause.ENTITY_ATTACK) {
                    Entity attackingEntity = subEvent.getDamager();
                    Entity defendingEntity = subEvent.getEntity();

                    if (checkStunned(attackingEntity)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (attackingEntity instanceof Player) {
                        Hero attackingHero = plugin.getHeroManager().getHero((Player) attackingEntity);
                        HeroClass heroClass = attackingHero.getHeroClass();
                        if (attackingHero.hasEffect("Blackjack")) {
                            double chance = getSetting(heroClass, "stun-chance", 0.20);
                            if (random.nextDouble() < chance) {
                                int duration = getSetting(heroClass, "stun-duration", 5000);
                                stunnedEntities.put(defendingEntity.getEntityId(), System.currentTimeMillis() + duration);
                                String targetName = defendingEntity instanceof Player ? ((Player) defendingEntity).getName() : defendingEntity.getClass().getSimpleName().substring(5);
                                broadcast(attackingHero.getPlayer().getLocation(), "$1 stunned $2!", attackingHero.getPlayer().getName(), targetName);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onEntityTarget(EntityTargetEvent event) {
            if (checkStunned(event.getEntity())) {
                event.setCancelled(true);
            }
        }

    }

    public class SkillPlayerListener extends PlayerListener {

        @Override
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (checkStunned(event.getPlayer())) {
                event.setCancelled(true);
            }
        }

        @Override
        public void onPlayerMove(PlayerMoveEvent event) {
            if (checkStunned(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().teleport(event.getFrom());
            }
        }

    }

}
