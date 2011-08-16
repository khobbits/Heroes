package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.PoisonEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillPoison extends TargettedSkill {

    private String expireText;

    public SkillPoison(Heroes plugin) {
        super(plugin, "Poison");
        setDescription("Poisons your target");
        setUsage("/skill poison <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[]{"skill poison"});
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("duration", 10000); //in milliseconds
        node.setProperty("period", 2000); //in milliseconds
        node.setProperty("tick-damage", 1);
        node.setProperty("expire-text", "%target% has recovered from the poison!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, "expire-text", "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {

        Player player = hero.getPlayer();
        if (!(target instanceof Player) && !(target instanceof Creature)) {
            Messaging.send(player, "You need a target!");
            return false;
        }
        Hero targetHero = null;
        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            targetHero = getPlugin().getHeroManager().getHero(targetPlayer);
            if (targetHero.equals(hero)) {
                Messaging.send(player, "You need a target!");
                return false;
            }
            //Party check
            if (hero.getParty() != null) {
                if (hero.getParty().isPartyMember(targetHero)) {
                    Messaging.send(player, "You need a target!");
                    return false;
                }
            }
        }

        broadcastExecuteText(hero, target);
        long duration = getSetting(hero.getHeroClass(), "duration", 10000);
        long period = getSetting(hero.getHeroClass(), "period", 2000);
        int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 1);
        PoisonSkillEffect pEffect = new PoisonSkillEffect(this, "Poison", period, duration, tickDamage, player);
        if (targetHero != null) {
            targetHero.addEffect(pEffect);
        } else if (target instanceof Creature) {
            Creature creature = (Creature) target;
            getPlugin().getHeroManager().addCreatureEffect(creature, pEffect);
        }
        return true;
    }

    public class PoisonSkillEffect extends PoisonEffect {

        public PoisonSkillEffect(Skill skill, String name, long period, long duration, int tickDamage, Player applier) {
            super(skill, name, period, duration, tickDamage, applier);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
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
