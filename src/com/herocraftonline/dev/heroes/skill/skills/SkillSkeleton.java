package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Set;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillSkeleton extends ActiveSkill {

    private String expireText;

    private SummonEntityListener eListener;
    private SummonPlayerListener pListener;
    public SkillSkeleton(Heroes plugin) {
        super(plugin, "Summon");
        setDescription("Summons a skeleton to fight by your side");
        setUsage("/skill skeleton");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill skeleton"});

        eListener = new SummonEntityListener();
        pListener = new SummonPlayerListener();
        registerEvent(Type.ENTITY_DEATH, eListener, Priority.Monitor);
        registerEvent(Type.ENTITY_TARGET, eListener, Priority.Highest);
        registerEvent(Type.PLAYER_QUIT, pListener, Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("max-summons", 3);
        node.setProperty("duration", 60000);
        node.setProperty("expire-text", "The skeleton returns to it's hellish domain.");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, "expire-text", "The skeleton returns to it's hellish domain.");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        if (hero.getSummons().size() < getSetting(hero.getHeroClass(), "max-summons", 3)) {
            Creature skeleton = (Creature) player.getWorld().spawnCreature(player.getLocation(), CreatureType.SKELETON);
            long duration = getSetting(hero.getHeroClass(), "duration", 60000);
            getPlugin().getHeroManager().addCreatureEffect(skeleton, new SummonEffect(this, duration, hero));
            broadcastExecuteText(hero);
            Messaging.send(player, "You have succesfully summoned a skeleton to fight for you.");
            return true;
        }

        Messaging.send(player, "You can't control anymore summons!");
        return false;
    }

    public class SummonEffect extends ExpirableEffect implements Dispellable {

        private Hero summoner;

        public SummonEffect(Skill skill, long duration, Hero summoner) {
            super(skill, "Summon", duration);
            this.summoner = summoner;
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
            summoner.getSummons().add(creature);
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            summoner.getSummons().remove(creature);
            broadcast(creature.getLocation(), expireText);
            creature.remove();
        }
    }

    public class SummonEntityListener extends EntityListener {

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            if (!(event.getEntity() instanceof Creature)) return;
            Creature defender = (Creature) event.getEntity();
            Set<Hero> heroes = getPlugin().getHeroManager().getHeroes();
            for (Hero hero : heroes) {
                if (hero.getSummons().contains(defender)) {
                    hero.getSummons().remove(defender);
                }
            }
        }

        @Override
        public void onEntityTarget(EntityTargetEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Creature)) return;
            if (event.getTarget() instanceof Player) {
                Set<Hero> heroes = getPlugin().getHeroManager().getHeroes();
                for (Hero hero : heroes) {
                    if (hero.getSummons().contains((Creature) event.getEntity())) {
                        if (hero.getParty() != null) {
                            //Don't target party members either
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
            //Destroy any summoned creatures when the player exits
            Hero hero = getPlugin().getHeroManager().getHero(event.getPlayer());
            if (hero.getSummons().isEmpty()) return;
            for(Creature summon : hero.getSummons()) {
                if (summon instanceof Skeleton) {
                    Effect effect = getPlugin().getHeroManager().getCreatureEffect(summon, "Summon");
                    if (effect != null) {
                        getPlugin().getHeroManager().removeCreatureEffect(summon, effect);
                    } else {
                        summon.remove();
                    }
                }
            }
        }
    }
}
