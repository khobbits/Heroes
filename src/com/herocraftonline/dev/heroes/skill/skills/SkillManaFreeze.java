package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillManaFreeze extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillManaFreeze(Heroes plugin) {
        super(plugin, "ManaFreeze");
        setDescription("Stops your target regening mana");
        setUsage("/skill manafreeze");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill manafreeze" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 5000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% has stopped regenerating mana!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% is once again regenerating mana!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% has stopped regenerating mana!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% is once again regenerating mana!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (target instanceof Player) {
            broadcastExecuteText(hero, target);

            Hero targetHero = getPlugin().getHeroManager().getHero((Player) target);
            int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 5000);
            targetHero.addEffect(new ManaFreezeEffect(this, duration));
            return true;
        } else {
            return false;
        }
    }

    public class ManaFreezeEffect extends ExpirableEffect implements Dispellable {

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
