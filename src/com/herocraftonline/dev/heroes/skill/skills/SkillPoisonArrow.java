package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.PoisonEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillPoisonArrow extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillPoisonArrow(Heroes plugin) {
        super(plugin, "PoisonArrow");
        setDescription("You fire a poison arrow from your bow");
        setUsage("/skill parrow");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill parrow" });

        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("poison-duration", 10000); // 10 seconds in
        node.setProperty("duration", 60000); // milliseconds
        node.setProperty("period", 2000); // 2 seconds in milliseconds
        node.setProperty("tick-damage", 2);
        node.setProperty("apply-text", "%target% is poisoned!");
        node.setProperty("expire-text", "%target% has recovered from the poison!");
        return node;
    }

    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%target% is poisoned!").replace("%target%", "$1");
        expireText = getSetting(null, "expire-text", "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        return true;
    }

    public class SkillDamageListener extends EntityListener {

        private final Skill skill;

        public SkillDamageListener(Skill skill) {
            this.skill = skill;
        }

        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent))
                return;

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (!(subEvent.getDamager() instanceof Arrow))
                return;
            
            Arrow arrow = (Arrow) subEvent.getDamager();
            if (!(arrow.getShooter() instanceof Player))
                return;
            
            Player player = (Player) arrow.getShooter();
            Hero hero = getPlugin().getHeroManager().getHero(player);

            if (hero.hasEffect("PoisonArrow")) {
                long duration = getSetting(hero.getHeroClass(), "poison-duration", 10000);
                long period = getSetting(hero.getHeroClass(), "period", 2000);
                int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 2);
                arrowPoison apEffect = new arrowPoison(skill, period, duration, tickDamage, player);
                
                if (event.getEntity() instanceof Creature) {
                    getPlugin().getHeroManager().addCreatureEffect((Creature) event.getEntity(), apEffect);
                    hero.removeEffect(hero.getEffect("PoisonArrow"));
                } else if (event.getEntity() instanceof Player) {
                    Hero target = getPlugin().getHeroManager().getHero((Player) event.getEntity());
                    target.addEffect(apEffect);
                    hero.removeEffect(hero.getEffect("PoisonArrow"));
                }
            }
        }
    }
    public class arrowPoison extends PoisonEffect {

        public arrowPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "ArrowPoison", period, duration, tickDamage, applier);

        }

        public void apply(Hero hero) {
            super.apply(hero);
            broadcast(hero.getPlayer().getLocation(), applyText, hero.getPlayer().getDisplayName());
        }

        public void apply(Creature creature) {
            super.apply(creature);
            broadcast(creature.getLocation(), applyText, Messaging.getCreatureName(creature).toLowerCase());
        }

        public void remove(Hero hero) {
            super.remove(hero);
            broadcast(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getDisplayName());
        }

        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }
    }
}