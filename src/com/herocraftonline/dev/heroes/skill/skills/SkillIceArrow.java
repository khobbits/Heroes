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
import com.herocraftonline.dev.heroes.effects.common.ImbueEffect;
import com.herocraftonline.dev.heroes.effects.common.SlowEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillIceArrow extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillIceArrow(Heroes plugin) {
        super(plugin, "IceArrow");
        setDescription("You fire a icy arrow from your bow");
        setUsage("/skill iarrow");
        setArgumentRange(0, 0);
        setIdentifiers("skill iarrow", "skill icearrow");
        setTypes(SkillType.BUFF, SkillType.ICE, SkillType.SILENCABLE);

        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("slow-duration", 5000); // 5 seconds
        node.set("speed-multiplier", 2);
        node.set(Setting.DURATION.node(), 60000); // milliseconds
        node.set("attacks", 1); // How many attacks the buff lasts for.
        node.set(Setting.USE_TEXT.node(), "%hero% imbues their arrows with ice!");
        node.set(Setting.APPLY_TEXT.node(), "%target% is slowed by ice!");
        node.set(Setting.EXPIRE_TEXT.node(), "%hero%'s arrows are no longer imbued with ice!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        setUseText("%hero% imbues their arrows with ice!".replace("%hero%", "$1"));
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT.node(), "%target% is slowed by %hero%s !").replace("%target%", "$1").replace("%hero%", "$2");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT.node(), "%hero%'s arrows are no longer imbued with ice!").replace("%hero%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 60000, false);
        int numAttacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
        hero.addEffect(new IceArrowBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    public class IceArrowBuff extends ImbueEffect {

        public IceArrowBuff(Skill skill, long duration, int numAttacks) {
            super(skill, "SlowArrowBuff", duration, numAttacks);
            this.types.add(EffectType.ICE);
            setDescription("ice");
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
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent) || !(event.getEntity() instanceof LivingEntity)) {
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

            if (hero.hasEffect("SlowArrowBuff")) {
                long duration = SkillConfigManager.getUseSetting(hero, skill, "slow-duration", 10000, false);
                int amplifier = SkillConfigManager.getUseSetting(hero, skill, "speed-multiplier", 2, false);
                SlowEffect iceSlowEffect = new SlowEffect(skill, duration, amplifier, false, applyText, "$1 is no longer slowed.", hero);
                LivingEntity target = (LivingEntity) event.getEntity();
                if (target instanceof Player) {
                    Hero tHero = plugin.getHeroManager().getHero((Player) target);
                    tHero.addEffect(iceSlowEffect);
                    broadcast(target.getLocation(), applyText, tHero.getPlayer().getDisplayName(), player.getDisplayName());
                } else {
                    plugin.getEffectManager().addEntityEffect(target, iceSlowEffect);
                    broadcast(target.getLocation(), applyText, Messaging.getLivingEntityName(target), player.getDisplayName());
                }
                checkBuff(hero);
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }

        private void checkBuff(Hero hero) {
            IceArrowBuff iaBuff = (IceArrowBuff) hero.getEffect("SlowArrowBuff");
            iaBuff.useApplication();
            if (iaBuff.hasNoApplications()) {
                hero.removeEffect(iaBuff);
            }
        }
    }
}