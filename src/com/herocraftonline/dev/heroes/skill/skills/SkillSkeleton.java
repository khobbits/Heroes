package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.SummonEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSkeleton extends ActiveSkill {

    private String expireText;

    private SummonEntityListener eListener;
    private SummonPlayerListener pListener;

    public SkillSkeleton(Heroes plugin) {
        super(plugin, "Skeleton");
        setDescription("Summons a skeleton to fight by your side");
        setUsage("/skill skeleton");
        setArgumentRange(0, 0);
        setIdentifiers("skill skeleton");
        setTypes(SkillType.DARK, SkillType.SUMMON, SkillType.SILENCABLE);

        eListener = new SummonEntityListener();
        pListener = new SummonPlayerListener();
        registerEvent(Type.ENTITY_DEATH, eListener, Priority.Monitor);
        registerEvent(Type.ENTITY_TARGET, eListener, Priority.Highest);
        registerEvent(Type.ENTITY_COMBUST, eListener, Priority.Highest);
        registerEvent(Type.ENTITY_DAMAGE, eListener, Priority.Monitor);
        registerEvent(Type.PLAYER_QUIT, pListener, Priority.Lowest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("max-summons", 3);
        node.setProperty(Setting.MAX_DISTANCE.node(), 5);
        node.setProperty(Setting.DURATION.node(), 60000);
        node.setProperty(Setting.EXPIRE_TEXT.node(), "The skeleton returns to it's hellish domain.");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "The skeleton returns to it's hellish domain.");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        if (hero.getSummons().size() < getSetting(hero.getHeroClass(), "max-summons", 3)) {
            int distance = getSetting(hero.getHeroClass(), Setting.MAX_DISTANCE.node(), 5);
            Location castLoc = player.getTargetBlock((HashSet<Byte>) null, distance).getLocation();
            Creature skeleton = (Creature) player.getWorld().spawnCreature(castLoc, CreatureType.SKELETON);
            long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 60000);
            plugin.getHeroManager().addCreatureEffect(skeleton, new SummonEffect(this, duration, hero, expireText));
            broadcastExecuteText(hero);
            Messaging.send(player, "You have succesfully summoned a skeleton to fight for you.");
            return true;
        }

        Messaging.send(player, "You can't control anymore skeletons!");
        return false;
    }

    public class SummonEntityListener extends EntityListener {

        @Override
        public void onEntityCombust(EntityCombustEvent event) {
            if (!(event.getEntity() instanceof Skeleton) || event.isCancelled())
                return;
            Creature creature = (Creature) event.getEntity();
            // Don't allow summoned creatures to combust
            if (plugin.getHeroManager().creatureHasEffect(creature, "Summon")) {
                event.setCancelled(true);
            }
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent))
                return;
            if (event.getEntity() instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) event.getEntity());
                // If this hero has no summons then ignore the event
                if (hero.getSummons().isEmpty())
                    return;

                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                LivingEntity damager = null;
                // Lets get the damager
                if (subEvent.getDamager() instanceof Projectile) {
                    damager = ((Projectile) subEvent.getDamager()).getShooter();
                } else if (subEvent.getEntity() instanceof LivingEntity) {
                    damager = (LivingEntity) subEvent.getDamager();
                }
                if (damager == null)
                    return;

                // Loop through the hero's summons and set the target
                for (Creature creature : hero.getSummons()) {
                    if (!(creature instanceof Skeleton)) {
                        continue;
                    }
                    creature.setTarget(damager);
                }
            } else if (event.getEntity() instanceof LivingEntity) {
                // If a creature is being damaged, lets see if a player is dealing the damage to see if we need to make
                // the summon aggro
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                Player player = null;
                if (subEvent.getDamager() instanceof Player) {
                    player = (Player) subEvent.getDamager();
                } else if (subEvent.getDamager() instanceof Projectile) {
                    if (((Projectile) subEvent.getDamager()).getShooter() instanceof Player) {
                        player = (Player) ((Projectile) subEvent.getDamager()).getShooter();
                    }
                }

                if (player == null)
                    return;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.getSummons().isEmpty())
                    return;
                for (Creature creature : hero.getSummons()) {
                    if (!(creature instanceof Skeleton)) {
                        continue;
                    }
                    creature.setTarget((LivingEntity) event.getEntity());
                }
            }
        }

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            if (!(event.getEntity() instanceof Creature))
                return;
            Creature defender = (Creature) event.getEntity();
            Collection<Hero> heroes = plugin.getHeroManager().getHeroes();
            for (Hero hero : heroes) {
                if (hero.getSummons().contains(defender) && defender instanceof Skeleton) {
                    hero.getSummons().remove(defender);
                }
            }
        }

        @Override
        public void onEntityTarget(EntityTargetEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Creature))
                return;
            if (event.getTarget() instanceof Player) {
                for (Hero hero : plugin.getHeroManager().getHeroes()) {
                    if (hero.getSummons().contains(event.getEntity())) {
                        if (hero.getParty() != null) {
                            // Don't target party members either
                            for (Hero pHero : hero.getParty().getMembers()) {
                                if (pHero.getPlayer().equals(event.getTarget())) {
                                    event.setCancelled(true);
                                }
                            }
                        } else if (hero.getPlayer().equals(event.getTarget())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    public class SummonPlayerListener extends PlayerListener {

        @Override
        public void onPlayerQuit(PlayerQuitEvent event) {
            // Destroy any summoned creatures when the player exits
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (hero.getSummons().isEmpty())
                return;
            for (Creature summon : hero.getSummons()) {
                if (summon instanceof Skeleton) {
                    Effect effect = plugin.getHeroManager().getCreatureEffect(summon, "Summon");
                    if (effect != null) {
                        plugin.getHeroManager().removeCreatureEffect(summon, effect);
                    } else {
                        summon.remove();
                    }
                }
            }
        }
    }
}
