package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.SilenceEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSilence extends TargettedSkill {
    
    public SkillSilence(Heroes plugin) {
        super(plugin, "Silence");
        
        setDescription("Silences your target, making them unable to use some skills");
        setUsage("/skill silence [target]");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill silence" });
        
        setTypes(SkillType.DEBUFF, SkillType.SILENCABLE);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 5000);
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% is no longer silenced!");
        return node;
    }
    
    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        
        if (!(target instanceof Player) || target.equals(player)) {
            Messaging.send(player, "Invalid target!");
            return false;
        }
        
        //Check if the target is damagable
        if (!damageCheck(player, target))
            return false;
        
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 5000);
        SilenceEffect sEffect = new SilenceEffect(this, duration);
        getPlugin().getHeroManager().getHero((Player) target).addEffect(sEffect);
        broadcastExecuteText(hero, target);
        return true;
    }
}
