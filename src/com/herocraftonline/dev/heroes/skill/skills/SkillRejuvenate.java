package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.effects.Beneficial;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.PeriodicEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillRejuvenate extends TargettedSkill {

    public SkillRejuvenate(Heroes plugin) {
        super(plugin, "Rejuvenate");
        setDescription("Heals the target over time");
        setUsage("/skill rejuvenate <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[]{"skill rejuvenate"});
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("heal", 1);
        node.setProperty("period", 3000);
        node.setProperty("duration", 21000);
        return node;
    }
    
    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target instanceof Player) {
            Hero targetHero = getPlugin().getHeroManager().getHero((Player) target);

            if ( targetHero.getHealth() >= targetHero.getMaxHealth()) {
                Messaging.send(player, "Target is already fully healed.");
                return false;
            }
            
            long period = getSetting(hero.getHeroClass(), "period", 3000);
            long duration = getSetting(hero.getHeroClass(), "duration", 21000);
            int tickHealth = getSetting(hero.getHeroClass(), "heal", 1);
            RejuvenateEffect rEffect = new RejuvenateEffect(this, period, duration, tickHealth);
            targetHero.addEffect(rEffect);
            broadcastExecuteText(hero, target);
            return true;
        }
        
        Messaging.send(player, "You must target a player!");
        return false;
    }
    
    public class RejuvenateEffect extends PeriodicEffect implements Dispellable, Beneficial {

        private final int tickHealth;
        
        public RejuvenateEffect(Skill skill, long period, long duration, int tickHealth) {
            super(skill, "Rejuvenate", period, duration);
            this.tickHealth = tickHealth;
        }
        
        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            Messaging.send(player, "You begin rejuvenating health!");
        }
        
        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            Messaging.send(player, "You are no longer regaining health!");
        }
        
        @Override
        public void tick(Hero hero) {
            HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, tickHealth, this.getSkill());
            getPlugin().getServer().getPluginManager().callEvent(hrhEvent);
            if (hrhEvent.isCancelled()) return;

            hero.setHealth(hero.getHealth() + hrhEvent.getAmount());
            hero.syncHealth();
        }
    }
}
