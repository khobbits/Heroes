package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillCurse extends TargettedSkill {

    private String applyText;
    private String expireText;
    private String missText;

    public SkillCurse(Heroes plugin) {
        super(plugin, "Curse");
        setDescription("You curse the target for $1 seconds, giving their attacks a $2% miss chance.");
        setUsage("/skill curse <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill curse");
        setTypes(SkillType.DARK, SkillType.SILENCABLE, SkillType.HARMFUL, SkillType.DEBUFF);

        registerEvent(Type.CUSTOM_EVENT, new SkillEventListener(), Priority.Highest);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 5000); // in milliseconds
        node.set("miss-chance", .50); // decimal representation of miss-chance
        node.set("miss-text", "%target% misses an attack!");
        node.set(Setting.APPLY_TEXT.node(), "%target% has been cursed!");
        node.set(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the curse!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        missText = SkillConfigManager.getRaw(this, "miss-text", "%target% misses an attack!").replace("%target%", "$1");
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT.node(), "%target% has been cursed!").replace("%target%", "$1");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT.node(), "%target% has recovered from the curse!").replace("%target%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 5000, false);
        double missChance = SkillConfigManager.getUseSetting(hero, this, "miss-chance", .50, false);
        CurseEffect cEffect = new CurseEffect(this, duration, missChance);

        if (target instanceof Player)
            plugin.getHeroManager().getHero((Player) target).addEffect(cEffect);
        else
            plugin.getEffectManager().addEntityEffect(target, cEffect);

        return SkillResult.NORMAL;

    }

    public class CurseEffect extends ExpirableEffect {

        private final double missChance;

        public CurseEffect(Skill skill, long duration, double missChance) {
            super(skill, "Curse", duration);
            this.missChance = missChance;
            this.types.add(EffectType.HARMFUL);
            this.types.add(EffectType.DISPELLABLE);
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

        public double getMissChance() {
            return missChance;
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

    public class SkillEventListener extends HeroesEventListener {

        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled() || event.getDamage() == 0) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Hero hero = null;
            LivingEntity lEntity = null;

            if (event.getDamager() instanceof Player) {
                hero = plugin.getHeroManager().getHero((Player) event.getDamager());
            } else if (event.getDamager() instanceof LivingEntity) {
                lEntity = (LivingEntity) event.getDamager();
            } else if (event.getDamager() instanceof Projectile) {
                LivingEntity shooter = ((Projectile) event.getDamager()).getShooter();
                if (shooter == null) {
                    Heroes.debug.stopTask("HeroesSkillListener");
                    return;
                }
                if (shooter instanceof Player) {
                    hero = plugin.getHeroManager().getHero((Player) shooter);
                } else if (shooter instanceof LivingEntity) {
                    lEntity = (LivingEntity) shooter;
                }
            }

            if (hero != null) {
                if (hero.hasEffect("Curse")) {
                    CurseEffect cEffect = (CurseEffect) hero.getEffect("Curse");
                    if (Util.rand.nextDouble() < cEffect.missChance) {
                        event.setCancelled(true);
                        broadcast(hero.getPlayer().getLocation(), missText, hero.getPlayer().getDisplayName());
                    }
                }
            } else if (lEntity != null) {
                if (plugin.getEffectManager().entityHasEffect(lEntity, "Curse")) {
                    CurseEffect cEffect = (CurseEffect) plugin.getEffectManager().getEntityEffect(lEntity, "Curse");
                    if (Util.rand.nextDouble() < cEffect.missChance) {
                        event.setCancelled(true);
                        broadcast(lEntity.getLocation(), missText, Messaging.getLivingEntityName(lEntity).toLowerCase());
                    }
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }

    @Override
    public String getDescription(Hero hero) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
        double chance = SkillConfigManager.getUseSetting(hero, this, "miss-chance", .5, false);
        return getDescription().replace("$1", duration / 1000 + "").replace("$2", chance * 100 + "");
    }
}
