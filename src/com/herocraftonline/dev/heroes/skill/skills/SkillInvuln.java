package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.common.InvulnerabilityEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillInvuln extends ActiveSkill {

    public SkillInvuln(Heroes plugin) {
        super(plugin, "Invuln");
        setDescription("Grants total damage immunity");
        setUsage("/skill invuln");
        setArgumentRange(0, 0);
        setIdentifiers("skill invuln");
        setTypes(SkillType.FORCE, SkillType.BUFF, SkillType.SILENCABLE, SkillType.COUNTER);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% has become invulnerable!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% is once again vulnerable!");
        return node;
    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        // Remove any harmful effects on the caster
        for (Effect effect : hero.getEffects()) {
            if (effect.isType(EffectType.HARMFUL)) {
                hero.removeEffect(effect);
            }
        }
        hero.addEffect(new InvulnerabilityEffect(this, duration));
        return true;
    }
}
