package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.common.SafeFallEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;
import com.iCo6.util.Messaging;

public class SkillSafefallOther extends TargettedSkill {

    public SkillSafefallOther(Heroes plugin) {
        super(plugin, "SafefallOther");
        setDescription("Stops your target from taking fall damage for a short amount of time");
        setUsage("/skill safefallother <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill safefallother");
        setTypes(SkillType.MOVEMENT, SkillType.BUFF, SkillType.SILENCABLE);

    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% has gained safefall!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has lost safefall!");
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (!(target instanceof Player) || hero.getPlayer().equals(target)) {
            Messaging.send(hero.getPlayer(), "Invalid target!");
            return false;
        }

        Hero targetHero = plugin.getHeroManager().getHero((Player) target);
        broadcastExecuteText(hero, target);
        int duration = getSetting(hero, Setting.DURATION.node(), 10000, false);
        targetHero.addEffect(new SafeFallEffect(this, duration));

        return true;

    }

}
