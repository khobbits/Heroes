package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillAssassinsBlade extends ActiveSkill {

    private String applyText;
    private String expireText;
    
    public SkillAssassinsBlade(Heroes plugin) {
        super(plugin, "AssassinsBlade");
        setDescription("You dab your blade with deadly poison");
        setUsage("/skill ablade");
        setArgumentRange(0, 0);
        setIdentifiers("skill ablade", "skill assassinsblade");
        
        setTypes(SkillType.BUFF);
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("weapons", Util.defaultWeapons);
        node.setProperty("buff-duration", 600000); // 10 minutes in milliseconds
        node.setProperty("poison-duration", 10000); // 10 seconds in milliseconds
        node.setProperty(Setting.PERIOD.node(), 2000); // 2 seconds in milliseconds
        node.setProperty("tick-damage", 2);
        node.setProperty("attacks", 1); //How many attacks the buff lasts for.
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is poisoned!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is poisoned!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!").replace("%target%", "$1");
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        long duration = getSetting(hero.getHeroClass(), "buff-duration", 600000);
        int numAttacks = getSetting(hero.getHeroClass(), "attacks", 1);
        hero.addEffect(new AssassinBladeBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return true;
    }

    public class SkillDamageListener extends EntityListener {
        
        private final Skill skill;
        
        public SkillDamageListener(Skill skill) {
            this.skill = skill;
        }
        
        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent))
                return;
            
            //If our target isn't a creature or player lets exit
            if (!(event.getEntity() instanceof Creature) && !(event.getEntity() instanceof Player))
                return;
            
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (!(subEvent.getDamager() instanceof Player))
                return;
            
            Player player = (Player) subEvent.getDamager();
            ItemStack item = player.getItemInHand();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (!getSetting(hero.getHeroClass(), "weapons", Util.defaultWeapons).contains(item.getType().name()))
                return;
            
            if (hero.hasEffect("PoisonBlade")) {
                long duration = getSetting(hero.getHeroClass(), "poison-duration", 10000);
                long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 2000);
                int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 2);
                AssassinsPoison apEffect = new AssassinsPoison(skill, period, duration, tickDamage, player);
                if (event.getEntity() instanceof Creature) {
                    plugin.getHeroManager().addCreatureEffect((Creature) event.getEntity(), apEffect);
                    checkBuff(hero);
                } else if (event.getEntity() instanceof Player) {
                    Hero target = plugin.getHeroManager().getHero((Player) event.getEntity());
                    target.addEffect(apEffect);
                    checkBuff(hero);
                }
            }
        }
        
        private void checkBuff(Hero hero) {
            AssassinBladeBuff abBuff = (AssassinBladeBuff) hero.getEffect("PoisonBlade");
            abBuff.applicationsLeft -= 1;
            if (abBuff.applicationsLeft < 1)
                hero.removeEffect(abBuff);
        }
    }
    
    public class AssassinBladeBuff extends ExpirableEffect {

        private int applicationsLeft = 1;
        
        public AssassinBladeBuff(Skill skill, long duration, int numAttacks) {
            super(skill, "PoisonBlade", duration);
            this.applicationsLeft = numAttacks;
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.POISON);
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
            Messaging.send(hero.getPlayer(), "Your blade is no longer poisoned!");
        }
    }
    
    public class AssassinsPoison extends PeriodicDamageEffect {

        public AssassinsPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "AssassinsPoison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
        }
        
        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            broadcast(hero.getPlayer().getLocation(), applyText, hero.getPlayer().getDisplayName());
        }
        
        @Override
        public void apply(Creature creature) {
            super.apply(creature);
            broadcast(creature.getLocation(), applyText, Messaging.getCreatureName(creature).toLowerCase());
        }
        
        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            broadcast(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getDisplayName());
        }
        
        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }
    }
}
