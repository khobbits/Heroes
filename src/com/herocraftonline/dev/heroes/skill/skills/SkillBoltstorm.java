package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillBoltstorm extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillBoltstorm(Heroes plugin) {
        super(plugin, "Boltstorm");
        setDescription("Calls bolts of lightning down upon nearby enemies.");
        setUsage("/skill boltstorm");
        setArgumentRange(0, 0);
        setIdentifiers("skill boltstorm");
        setTypes(SkillType.LIGHTNING, SkillType.SILENCABLE, SkillType.DAMAGING);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.RADIUS.node(), 7); // radius
        node.setProperty(Setting.DURATION.node(), 10000); // in milliseconds
        node.setProperty(Setting.PERIOD.node(), 1000); // in milliseconds
        node.setProperty(Setting.DAMAGE.node(), 4); // Per-tick damage
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% has summoned a boltstorm!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero%'s boltstorm has subsided!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% has summoned a boltstorm!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero%'s boltstorm has subsided!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        int period = getSetting(hero, Setting.PERIOD.node(), 1000, true);
        int duration = getSetting(hero, Setting.DURATION.node(), 10000, false);
        hero.addEffect(new BoltStormEffect(this, period, duration));
        return true;
    }

    public class BoltStormEffect extends PeriodicExpirableEffect {

        public BoltStormEffect(Skill skill, long period, long duration) {
            super(skill, "Boltstorm", period, duration);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.LIGHTNING);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);

            Player player = hero.getPlayer();
            int range = getSetting(hero, Setting.RADIUS.node(), 7, false);

            List<LivingEntity> targets = new ArrayList<LivingEntity>();
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (!(entity instanceof LivingEntity)) {
                    continue;
                }

                LivingEntity target = (LivingEntity) entity;

                // never target the caster
                if (target.equals(player) || hero.getSummons().contains(target)) {
                    continue;
                }

                // check if the target is damagable
                if (!damageCheck(player, target)) {
                    continue;
                }

                targets.add(target);
            }

            if (targets.isEmpty())
                return;

            int damage = getSetting(hero, Setting.DAMAGE.node(), 4, false);
            LivingEntity target = targets.get(Util.rand.nextInt(targets.size()));
            addSpellTarget(target, hero);

            target.getWorld().strikeLightningEffect(target.getLocation());
            target.damage(damage, player);

        }
    }
}
