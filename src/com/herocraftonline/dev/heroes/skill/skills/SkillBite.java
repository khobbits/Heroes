package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

public class SkillBite extends TargettedSkill {
    
    private String applyText;
    private String expireText;
    
    public SkillBite(Heroes plugin) {
        super(plugin, "Bite");
        setDescription("Deals physical damage to the target");
        setUsage("/skill bite <target>");
        setArgumentRange(0, 1);
        
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);
        
        setIdentifiers(new String[] { "skill bite" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 10);
        node.setProperty(Setting.MAX_DISTANCE.node(), 2);
        node.setProperty(Setting.DURATION.node(), 15000);
        node.setProperty(Setting.PERIOD.node(), 3000);
        node.setProperty("tick-damage", 1);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is bleeding from a greivous wound!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!");
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is bleeding from a greivous wound!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has stopped bleeding!").replace("%target%", "$1");
    }
    
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target.equals(player) || hero.getSummons().contains(target)) {
            Messaging.send(player, "Invalid Target");
            return false;
        }

        //Check if the target is damagable
        if (!damageCheck(player, target))
            return false;

        //Damage the target
        int damage = getSetting(hero.getHeroClass(), Setting.DAMAGE.node(), 10);
        addSpellTarget(target, hero);
        target.damage(damage, player);
        
        //Apply our effect
        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 15000);
        long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 3000);
        int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 1);
        BiteBleedEffect bbEffect = new BiteBleedEffect(this, period, duration, tickDamage, player);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(bbEffect);
        } else if (target instanceof Creature) {
            plugin.getHeroManager().addCreatureEffect((Creature) target, bbEffect);
        }
        
        broadcastExecuteText(hero, target);
        return true;
    }
    
    public class BiteBleedEffect extends PeriodicDamageEffect {

        public BiteBleedEffect(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "BiteBleed", period, duration, tickDamage, applier);
            this.types.add(EffectType.BLEED);
        }
        
        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);

            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }
    }
}