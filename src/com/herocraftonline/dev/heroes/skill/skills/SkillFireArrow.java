package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.common.CombustEffect;
import com.herocraftonline.dev.heroes.effects.common.ImbueEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillFireArrow extends ActiveSkill {

    public SkillFireArrow(Heroes plugin) {
        super(plugin, "FireArrow");
        setDescription("Your next $1 arrows will light the target on fire!");
        setUsage("/skill firearrow");
        setArgumentRange(0, 0);
        setIdentifiers("skill firearrow", "skill farrow");
        setTypes(SkillType.FIRE, SkillType.BUFF);
        Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 60000); // milliseconds
        node.set("attacks", 1); // How many attacks the buff lasts for.
        node.set(Setting.DAMAGE.node(), 4);
        node.set("fire-ticks", 100);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 600000, false);
        int numAttacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
        hero.addEffect(new FireArrowBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    public class FireArrowBuff extends ImbueEffect {

        public FireArrowBuff(Skill skill, long duration, int numAttacks) {
            super(skill, "FireArrowBuff", duration, numAttacks);
            this.types.add(EffectType.FIRE);
            setDescription("fire");
        }
    }

    public class SkillEntityListener implements Listener {

        private final Skill skill;

        public SkillEntityListener(Skill skill) {
            this.skill = skill;
        }

        @EventHandler()
        public void onEntityShoot(EntityShootBowEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
                return;
            }
            Hero hero = plugin.getHeroManager().getHero((Player) event.getEntity());
            if (hero.hasEffect("FireArrowBuff")) {
                event.getProjectile().setFireTicks(100);
            }
        }

        @EventHandler()
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent) || !(event.getEntity() instanceof LivingEntity)) {
                return;
            }

            Entity projectile = ((EntityDamageByEntityEvent) event).getDamager();
            if (!(projectile instanceof Arrow) || !(((Projectile) projectile).getShooter() instanceof Player)) {
                return;
            }

            Player player = (Player) ((Projectile) projectile).getShooter();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (!hero.hasEffect("FireArrowBuff")) {
                return;
            }

            LivingEntity entity = (LivingEntity) event.getEntity();
            addSpellTarget(entity, hero);
            if (!damageCheck((Player) player, entity)) {
                event.setCancelled(true);
                return;
            }

            //Get the duration of the fire damage
            int fireTicks = SkillConfigManager.getUseSetting(hero, skill, "fire-ticks", 100, false);
            //Light the target on fire
            entity.setFireTicks(fireTicks);
            checkBuff(hero);
            //Add our combust effect so we can track fire-tick damage
            if (entity instanceof Player) {
                Hero targetHero = plugin.getHeroManager().getHero((Player) entity);
                targetHero.addEffect(new CombustEffect(skill, player));
            } else
                plugin.getEffectManager().addEntityEffect(entity, new CombustEffect(skill, player));


        }

        private void checkBuff(Hero hero) {
            FireArrowBuff faBuff = (FireArrowBuff) hero.getEffect("FireArrowBuff");
            faBuff.useApplication();
            if (faBuff.hasNoApplications())
                hero.removeEffect(faBuff);
        }
    }

    @Override
    public String getDescription(Hero hero) {
        int attacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
        return getDescription().replace("$1", attacks + "");
    }
}
