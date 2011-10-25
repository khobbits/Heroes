package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicHealEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillRejuvenate extends TargettedSkill {

    private String expireText;
    private String applyText;

    public SkillRejuvenate(Heroes plugin) {
        super(plugin, "Rejuvenate");
        setDescription("Heals the target over time");
        setUsage("/skill rejuvenate <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill rejuvenate", "skill rejuv");
        setTypes(SkillType.BUFF, SkillType.HEAL, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("tick-heal", 1);
        node.setProperty(Setting.PERIOD.node(), 3000);
        node.setProperty(Setting.DURATION.node(), 21000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is rejuvenating health!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has stopped rejuvenating health!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is rejuvenating health!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has stopped rejuvenating health!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target instanceof Player) {
            Hero targetHero = plugin.getHeroManager().getHero((Player) target);

            if (targetHero.getHealth() >= targetHero.getMaxHealth()) {
                Messaging.send(player, "Target is already fully healed.");
                return false;
            }

            long period = getSetting(hero, Setting.PERIOD.node(), 3000, true);
            long duration = getSetting(hero, Setting.DURATION.node(), 21000, false);
            int tickHealth = getSetting(hero, "tick-heal", 1, false);
            RejuvenateEffect rEffect = new RejuvenateEffect(this, period, duration, tickHealth, player);
            targetHero.addEffect(rEffect);
            return true;
        }

        Messaging.send(player, "You must target a player!");
        return false;
    }

    public class RejuvenateEffect extends PeriodicHealEffect {

        public RejuvenateEffect(Skill skill, long period, long duration, int tickHealth, Player applier) {
            super(skill, "Rejuvenate", period, duration, tickHealth, applier);
            this.types.add(EffectType.DISPELLABLE);
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
    }
}
