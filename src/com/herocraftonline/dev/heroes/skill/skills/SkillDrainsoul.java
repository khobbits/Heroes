package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillDrainsoul extends TargettedSkill {

    public SkillDrainsoul(Heroes plugin) {
        super(plugin, "Drainsoul");
        setDescription("Absorb health from target");
        setUsage("/skill drainsoul <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill drainsoul");
        setTypes(SkillType.DARK, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("absorb-amount", 4);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        int absorbAmount = getSetting(hero, "absorb-amount", 4, false);

        hero.setHealth(hero.getHealth() + absorbAmount);
        hero.syncHealth();
        addSpellTarget(target, hero);
        target.damage(absorbAmount, player);

        broadcastExecuteText(hero, target);
        return true;
    }

}
