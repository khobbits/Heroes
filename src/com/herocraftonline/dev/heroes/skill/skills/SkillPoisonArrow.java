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
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.effects.common.ImbueEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
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
        setIdentifiers("skill parrow", "skill poisonarrow");
        setTypes(SkillType.BUFF);

        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("poison-duration", 10000); // 10 seconds in
        node.setProperty(Setting.DURATION.node(), 60000); // milliseconds
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
        long duration = getSetting(hero, Setting.DURATION.node(), 600000, false);
        int numAttacks = getSetting(hero, "attacks", 1, false);
        hero.addEffect(new PoisonArrowBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return true;
    }

    public class ArrowPoison extends PeriodicDamageEffect {
        
        public ArrowPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "ArrowPoison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
            addMobEffect(19, (int) (duration / 1000) * 20, 0, true);
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

    public class PoisonArrowBuff extends ImbueEffect {

        public PoisonArrowBuff(Skill skill, long duration, int numAttacks) {
            super(skill, "PoisonArrowBuff", duration, numAttacks);
            this.types.add(EffectType.POISON);
            setDescription("poison");
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

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (!(subEvent.getDamager() instanceof Arrow)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Arrow arrow = (Arrow) subEvent.getDamager();
            if (!(arrow.getShooter() instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Player player = (Player) arrow.getShooter();
            Hero hero = plugin.getHeroManager().getHero(player);

            if (hero.hasEffect("PoisonArrowBuff")) {
                long duration = getSetting(hero, "poison-duration", 10000, false);
                long period = getSetting(hero, Setting.PERIOD.node(), 2000, true);
                int tickDamage = getSetting(hero, "tick-damage", 2, false);
                ArrowPoison apEffect = new ArrowPoison(skill, period, duration, tickDamage, player);

                if (event.getEntity() instanceof Creature) {
                    plugin.getEffectManager().addCreatureEffect((Creature) event.getEntity(), apEffect);
                    checkBuff(hero);
                } else if (event.getEntity() instanceof Player) {
                    Hero target = plugin.getHeroManager().getHero((Player) event.getEntity());
                    target.addEffect(apEffect);
                    checkBuff(hero);
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }

        private void checkBuff(Hero hero) {
            PoisonArrowBuff paBuff = (PoisonArrowBuff) hero.getEffect("PoisonArrowBuff");
            if (paBuff.hasNoApplications())
                hero.removeEffect(paBuff);
        }
    }
}