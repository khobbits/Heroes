package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillManaFreeze extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillManaFreeze(Heroes plugin) {
        super(plugin);
        setName("ManaFreeze");
        setDescription("Stops your target regening mana");
        setUsage("/skill manafreeze");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill manafreeze");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty("duration", 5000);
        node.setProperty("apply-text", "%target% has stopped regenerating mana!");
        node.setProperty("expire-text", "%target% is once again regenerating mana!");
        return node;
    }

    @Override
    public void init() {
        applyText = getSetting(null, "apply-text", "%target% has stopped regenerating mana!").replace("%target%", "$1");
        expireText = getSetting(null, "expire-text", "%target% is once again regenerating mana!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (target instanceof Player) {
            Hero targetHero = plugin.getHeroManager().getHero((Player) target);
            int duration = getSetting(hero.getHeroClass(), "duration", 5000);
            targetHero.addEffect(new ManaFreezeEffect(this, duration));
            return true;
        } else {
            return false;
        }
    }

    public class ManaFreezeEffect extends ExpirableEffect {

        public ManaFreezeEffect(Skill skill, long duration) {
            super(skill, "ManaFreeze", duration);
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

    }
}
