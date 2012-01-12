package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.effects.common.ImbueEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPoisonArrow extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillPoisonArrow(Heroes plugin) {
        super(plugin, "PoisonArrow");
        setDescription("Your next $1 arrows will poison their target dealing $2 damage over $3 seconds.");
        setUsage("/skill parrow");
        setArgumentRange(0, 0);
        setIdentifiers("skill parrow", "skill poisonarrow");
        setTypes(SkillType.BUFF);

        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("poison-duration", 10000); // 10 seconds in
        node.set(Setting.DURATION.node(), 60000); // milliseconds
        node.set(Setting.PERIOD.node(), 2000); // 2 seconds in milliseconds
        node.set("tick-damage", 2);
        node.set("attacks", 1); // How many attacks the buff lasts for.
        node.set(Setting.APPLY_TEXT.node(), "%target% is poisoned!");
        node.set(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "%target% is poisoned!").replace("%target%", "$1");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 600000, false);
        int numAttacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
        hero.addEffect(new PoisonArrowBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    public class ArrowPoison extends PeriodicDamageEffect {
        
        public ArrowPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "ArrowPoison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
            addMobEffect(19, (int) (duration / 1000) * 20, 0, true);
        }

        @Override
        public void apply(LivingEntity lEntity) {
            super.apply(lEntity);
            broadcast(lEntity.getLocation(), applyText, Messaging.getLivingEntityName(lEntity).toLowerCase());
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(LivingEntity lEntity) {
            super.remove(lEntity);
            broadcast(lEntity.getLocation(), expireText, Messaging.getLivingEntityName(lEntity).toLowerCase());
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
            if (event.isCancelled() || !(event.getEntity() instanceof LivingEntity) || !(event instanceof EntityDamageByEntityEvent)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            LivingEntity target = (LivingEntity) event.getEntity();
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
                long duration = SkillConfigManager.getUseSetting(hero, skill, "poison-duration", 10000, false);
                long period = SkillConfigManager.getUseSetting(hero, skill, Setting.PERIOD, 2000, true);
                int tickDamage = SkillConfigManager.getUseSetting(hero, skill, "tick-damage", 2, false);
                ArrowPoison apEffect = new ArrowPoison(skill, period, duration, tickDamage, player);
                
                if (target instanceof Player) {
                    Hero hTarget = plugin.getHeroManager().getHero((Player) target);
                    hTarget.addEffect(apEffect);
                    checkBuff(hero);
                } else {
                    plugin.getEffectManager().addEntityEffect(target, apEffect);
                    checkBuff(hero);
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }

        private void checkBuff(Hero hero) {
            PoisonArrowBuff paBuff = (PoisonArrowBuff) hero.getEffect("PoisonArrowBuff");
            paBuff.useApplication();
            if (paBuff.hasNoApplications())
                hero.removeEffect(paBuff);
        }
    }

    @Override
    public String getDescription(Hero hero) {
        int attacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
        int duration = SkillConfigManager.getUseSetting(hero, this, "poison-duration", 10000, false);
        int period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 2000, false);
        int damage = SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1, false);
        damage = damage * duration / period;
        return getDescription().replace("$1", attacks + "").replace("$2", damage + "").replace("$3", duration / 1000 + "");
    }
}