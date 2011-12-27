package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.common.SafeFallEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSuperJump extends ActiveSkill {

    public SkillSuperJump(Heroes plugin) {
        super(plugin, "SuperJump");
        setDescription("Launches you into the air, and gives you short term safefall");
        setUsage("/skill superjump");
        setArgumentRange(0, 0);
        setIdentifiers("skill superjump");
        setTypes(SkillType.MOVEMENT, SkillType.PHYSICAL);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 5000);
        node.set("jump-force", 4.0);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        float jumpForce = (float) SkillConfigManager.getUseSetting(hero, this, "jump-force", 1.0, false);
        Vector v1 = new Vector(0, jumpForce, 0);
        Vector v = player.getVelocity().add(v1);
        player.setVelocity(v);
        player.setFallDistance(-8f);
        int duration = (int) SkillConfigManager.getUseSetting(hero, this, Setting.DURATION.node(), 5000, false);
        hero.addEffect(new SafeFallEffect(this, duration));
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }
}