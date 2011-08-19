package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Beneficial;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.PeriodicHealEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillRejuvenate extends TargettedSkill {

    private String expireText;
    private String applyText;
    
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
        node.setProperty("tick-heal", 1);
        node.setProperty("period", 3000);
        node.setProperty("duration", 21000);
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%target% is rejuvenating health!").replace("%target%", "$1");
        expireText = getSetting(null, "expire-text", "%target% has stopped rejuvenating health!").replace("%target%", "$1");
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
            int tickHealth = getSetting(hero.getHeroClass(), "tick-heal", 1);
            RejuvenateEffect rEffect = new RejuvenateEffect(this, period, duration, tickHealth, player);
            targetHero.addEffect(rEffect);
            return true;
        }
        
        Messaging.send(player, "You must target a player!");
        return false;
    }
    
    public class RejuvenateEffect extends PeriodicHealEffect implements Dispellable, Beneficial {

        
        public RejuvenateEffect(Skill skill, long period, long duration, int tickHealth, Player applier) {
            super(skill, "Rejuvenate", period, duration, tickHealth, applier);
        }
        
        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, new Object[] { player.getDisplayName()});
        }
        
        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, new Object[] { player.getDisplayName()});
        }
    }
}
