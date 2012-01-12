package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.common.WaterBreatheEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillGills extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillGills(Heroes plugin) {
        super(plugin, "Gills");
        setDescription("You are able to breath water for $1 seconds.");
        setUsage("/skill gills");
        setArgumentRange(0, 0);
        setIdentifiers("skill gills");
        setTypes(SkillType.SILENCABLE, SkillType.BUFF);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 30000);
        node.set(Setting.APPLY_TEXT.node(), "%hero% has grown a set of gills!");
        node.set(Setting.EXPIRE_TEXT.node(), "%hero% lost his gills!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "%hero% has grown a set of gills!").replace("%hero%", "$1");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%hero% lost his gills!").replace("%hero%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 30000, false);
        hero.addEffect(new WaterBreatheEffect(this, duration, applyText, expireText));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 1, false);
        return getDescription().replace("$1", duration / 1000 + "");
    }
}
