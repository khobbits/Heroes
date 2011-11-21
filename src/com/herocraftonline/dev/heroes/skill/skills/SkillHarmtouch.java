package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillHarmtouch extends TargettedSkill {

    public SkillHarmtouch(Heroes plugin) {
        super(plugin, "Harmtouch");
        setDescription("Deals direct damage to the target");
        setUsage("/skill harmtouch <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill harmtouch");
        setTypes(SkillType.DARK, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 10);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        int damage = getSetting(hero, Setting.DAMAGE.node(), 10, false);
        addSpellTarget(target, hero);
        target.damage(damage, player);
        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }
}
