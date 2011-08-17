package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillDamageEvent;
import com.herocraftonline.dev.heroes.effects.Beneficial;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillFlameshield extends ActiveSkill {

    private String applyText;
    private String expireText;
    private String skillBlockText;

    public SkillFlameshield(Heroes plugin) {
        super(plugin, "Flameshield");
        setDescription("Fire can't hurt you!");
        setUsage("/skill flameshield");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill flameshield"});

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
        registerEvent(Type.CUSTOM_EVENT, new HeroesSkillListener(), Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("duration", 5000);
        node.setProperty("apply-text", "%hero% conjured a shield of flames!");
        node.setProperty("expire-text", "%hero% lost his shield of flames!");
        node.setProperty("skill-block-text", "%name%'s flameshield has blocked %hero%'s %skill%.");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%hero% conjured a shield of flames!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% lost his shield of flames!").replace("%hero%", "$1");
        skillBlockText = getSetting(null, "skill-block-text", "%name%'s flameshield has blocked %hero%'s %skill%.").replace("%name%", "$1").replace("%hero%", "$2").replace("%hero%", "$3");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = getSetting(hero.getHeroClass(), "duration", 5000);
        hero.addEffect(new FlameshieldEffect(this, duration));

        return true;
    }

    public class FlameshieldEffect extends ExpirableEffect implements Dispellable, Beneficial {

        public FlameshieldEffect(Skill skill, long duration) {
            super(skill, "Flameshield", duration);
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
            if (event.isCancelled()) 
                return;
            
            if (event.getCause() != DamageCause.FIRE && event.getCause() != DamageCause.FIRE_TICK && event.getCause() != DamageCause.LAVA)
                return;
            
            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = getPlugin().getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    public class HeroesSkillListener extends HeroesEventListener {
        
        @Override
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled()) return;
            if (!event.getSkill().getName().toLowerCase().contains("fire") && !event.getSkill().getName().toLowerCase().contains("flame")) return;
            
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Hero hero = getPlugin().getHeroManager().getHero(player);
                if (hero.hasEffect("Flameshield")) {
                    String name = event.getDamager().getPlayer().getName();
                    String skillName = event.getSkill().getName().toLowerCase();
                    broadcast(event.getEntity().getLocation(), skillBlockText, new Object[] {player.getName(), name, skillName});
                    event.setCancelled(true);
                }
            } else if (event.getEntity() instanceof Creature) {
                Creature creature = (Creature) event.getEntity();
                if (getPlugin().getHeroManager().creatureHasEffect(creature, "Flameshield")) {
                    String name = event.getDamager().getPlayer().getName();
                    String skillName = event.getSkill().getName().toLowerCase();
                    broadcast(event.getEntity().getLocation(), skillBlockText, new Object[] {Messaging.getCreatureName(creature), name, skillName});
                    event.setCancelled(true);
                }
            }
        }
    }
}
