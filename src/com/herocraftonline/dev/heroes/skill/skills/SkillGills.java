package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.common.WaterBreatheEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillGills extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillGills(Heroes plugin) {
        super(plugin, "Gills");
        setDescription("Negate drowning damage");
        setUsage("/skill gills");
        setArgumentRange(0, 0);
        setIdentifiers("skill gills");
        setTypes(SkillType.SILENCABLE, SkillType.BUFF);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 30000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% has grown a set of gills!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% lost his gills!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% has grown a set of gills!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% lost his gills!").replace("%hero%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        int duration = getSetting(hero, Setting.DURATION.node(), 30000, false);
        hero.addEffect(new WaterBreatheEffect(this, duration, applyText, expireText));
        return SkillResult.NORMAL;
    }
}
