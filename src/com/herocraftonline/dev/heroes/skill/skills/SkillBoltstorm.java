package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBoltstorm extends ActiveSkill {

    private String applyText;
    private String expireText;
    private Random rand;

    public SkillBoltstorm(Heroes plugin) {
        super(plugin, "Boltstorm");
        setDescription("Calls bolts of lightning down upon nearby enemies.");
        setUsage("/skill boltstorm");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill boltstorm" });
        rand = new Random();
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
    public boolean use(Hero hero, String[] args) {
        int period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 1000);
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        hero.addEffect(new BoltStormEffect(this, period, duration));
        return true;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% has summoned a boltstorm!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero%'s boltstorm has subsided!").replace("%hero%", "$1");
    }

    public class BoltStormEffect extends PeriodicEffect {

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
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);

            Player player = hero.getPlayer();
            int range = getSetting(hero.getHeroClass(), Setting.RADIUS.node(), 7);

            List<LivingEntity> targets = new ArrayList<LivingEntity>();
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (!(entity instanceof LivingEntity))
                    continue;
                
                // never target the caster
                if (entity.equals(player) || hero.getSummons().contains(entity)) { 
                    continue;
                }
                
                //Check if the target is damagable
                if (!damageCheck(player, (LivingEntity) entity))
                    continue;
                
                targets.add((LivingEntity) entity);
            }
            if (targets.isEmpty())
                return;
            
            int damage = getSetting(hero.getHeroClass(), Setting.DAMAGE.node(), 4);
            LivingEntity target = targets.get(rand.nextInt(targets.size()));
            addSpellTarget(target, hero);

            target.getWorld().strikeLightningEffect(target.getLocation());
            target.damage(damage, player);

        }
    }
}
