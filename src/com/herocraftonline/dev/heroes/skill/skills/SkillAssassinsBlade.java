package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

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
        node.setProperty("weapons", Util.swords);
        node.setProperty("buff-duration", 600000); // 10 minutes in milliseconds
        node.setProperty("poison-duration", 10000); // 10 seconds in milliseconds
        node.setProperty(Setting.PERIOD.node(), 2000); // 2 seconds in milliseconds
        node.setProperty("tick-damage", 2);
        node.setProperty("attacks", 1); // How many attacks the buff lasts for.
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
        long duration = getSetting(hero, "buff-duration", 600000, false);
        int numAttacks = getSetting(hero, "attacks", 1, false);
        hero.addEffect(new AssassinBladeBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return true;
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

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Messaging.send(hero.getPlayer(), "Your blade is no longer poisoned!");
        }

        /**
         * @param applicationsLeft
         *            the applicationsLeft to set
         */
        public void setApplicationsLeft(int applicationsLeft) {
            this.applicationsLeft = applicationsLeft;
        }
    }

    public class AssassinsPoison extends PeriodicDamageEffect {

        public AssassinsPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "AssassinsPoison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
            broadcast(creature.getLocation(), applyText, Messaging.getCreatureName(creature).toLowerCase());
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }
    }

    public class SkillDamageListener extends EntityListener {

        private final Skill skill;

        public SkillDamageListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            // If our target isn't a creature or player lets exit
            if (!(event.getEntity() instanceof Creature) && !(event.getEntity() instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (!(subEvent.getDamager() instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Player player = (Player) subEvent.getDamager();
            ItemStack item = player.getItemInHand();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (!getSetting(hero, "weapons", Util.swords).contains(item.getType().name())) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            if (hero.hasEffect("PoisonBlade")) {
                long duration = getSetting(hero, "poison-duration", 10000, false);
                long period = getSetting(hero, Setting.PERIOD.node(), 2000, false);
                int tickDamage = getSetting(hero, "tick-damage", 2, false);
                AssassinsPoison apEffect = new AssassinsPoison(skill, period, duration, tickDamage, player);
                Entity target = event.getEntity();
                if (target instanceof Creature) {
                    plugin.getEffectManager().addCreatureEffect((Creature) target, apEffect);
                    checkBuff(hero);
                } else if (event.getEntity() instanceof Player) {
                    Hero targetHero = plugin.getHeroManager().getHero((Player) target);
                    targetHero.addEffect(apEffect);
                    checkBuff(hero);
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }

        private void checkBuff(Hero hero) {
            AssassinBladeBuff abBuff = (AssassinBladeBuff) hero.getEffect("PoisonBlade");
            abBuff.applicationsLeft -= 1;
            if (abBuff.applicationsLeft < 1) {
                hero.removeEffect(abBuff);
            }
        }
    }
}
