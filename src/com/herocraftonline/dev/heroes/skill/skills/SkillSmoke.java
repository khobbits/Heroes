package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.common.InvisibleEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSmoke extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillSmoke(Heroes plugin) {
        super(plugin, "Smoke");
        setDescription("You completely disappear from view");
        setUsage("/skill smoke");
        setArgumentRange(0, 0);
        setIdentifiers("skill smoke");
        setNotes("Note: Taking damage removes the effect");
        setTypes(SkillType.ILLUSION, SkillType.BUFF, SkillType.COUNTER, SkillType.STEALTHY);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 20000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% vanished in a cloud of smoke!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% reappeared!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% vanished in a cloud of smoke!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% reappeard!").replace("%hero%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        long duration = getSetting(hero, Setting.DURATION.node(), 20000, false);
        Player player = hero.getPlayer();
        player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.SMOKE, 4);
        hero.addEffect(new InvisibleEffect(this, duration, applyText, expireText));

        return SkillResult.NORMAL;
    }
}
