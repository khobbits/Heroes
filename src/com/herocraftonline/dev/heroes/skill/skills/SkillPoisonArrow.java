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
import com.herocraftonline.dev.heroes.effects.Beneficial;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.Harmful;
import com.herocraftonline.dev.heroes.effects.PoisonEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

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
        node.setProperty(Setting.DURATION.node(), 60000); // milliseconds
        node.setProperty(Setting.PERIOD.node(), 2000); // 2 seconds in milliseconds
        node.setProperty("tick-damage", 2);
        node.setProperty("attacks", 1); //How many attacks the buff lasts for.
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is poisoned!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
        return node;
    }

    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is poisoned!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    public boolean use(Hero hero, String[] args) {
        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 600000);
        int numAttacks = getSetting(hero.getHeroClass(), "attacks", 1);
        hero.addEffect(new PoisonArrowBuff(this, duration, numAttacks));
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

            if (hero.hasEffect("PoisonArrowBuff")) {
                long duration = getSetting(hero.getHeroClass(), "poison-duration", 10000);
                long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 2000);
                int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 2);
                ArrowPoison apEffect = new ArrowPoison(skill, period, duration, tickDamage, player);
                
                if (event.getEntity() instanceof Creature) {
                    getPlugin().getHeroManager().addCreatureEffect((Creature) event.getEntity(), apEffect);
                    checkBuff(hero);
                } else if (event.getEntity() instanceof Player) {
                    Hero target = getPlugin().getHeroManager().getHero((Player) event.getEntity());
                    target.addEffect(apEffect);
                    checkBuff(hero);
                }
            }
        }
        
        private void checkBuff(Hero hero) {
            PoisonArrowBuff paBuff = (PoisonArrowBuff) hero.getEffect("PoisonArrowBuff");
            paBuff.applicationsLeft -= 1;
            if (paBuff.applicationsLeft < 1)
                hero.removeEffect(paBuff);
        }
    }
    
    public class PoisonArrowBuff extends ExpirableEffect implements Beneficial {
        
        private int applicationsLeft = 1;
        
        public PoisonArrowBuff(Skill skill, long duration, int numAttacks) {
            super(skill, "PoisonArrowBuff", duration);
            this.applicationsLeft = numAttacks;
        }
        
        /**
         * @return the applicationsLeft
         */
        public int getApplicationsLeft() {
            return applicationsLeft;
        }

        /**
         * @param applicationsLeft the applicationsLeft to set
         */
        public void setApplicationsLeft(int applicationsLeft) {
            this.applicationsLeft = applicationsLeft;
        }

        @Override
        public void remove(Hero hero) {
            Messaging.send(hero.getPlayer(), "Your arrows are no longer poisoned");
        }
    }
    
    public class ArrowPoison extends PoisonEffect implements Harmful {

        public ArrowPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
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