package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillAntidote extends TargettedSkill {

    public SkillAntidote(Heroes plugin) {
        super(plugin, "Antidote");
        setDescription("Cures your target of poisons");
        setUsage("/skill antidote <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill antidote");
        setTypes(SkillType.SILENCABLE, SkillType.HEAL);
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target instanceof Player) {
            Hero targetHero = plugin.getHeroManager().getHero((Player) target);
            boolean cured = false;
            for (Effect effect : targetHero.getEffects()) {
                if (effect.isType(EffectType.POISON) && !effect.isType(EffectType.BENEFICIAL)) {
                    cured = true;
                    targetHero.removeEffect(effect);
                }
            }
            if (!cured) {
                Messaging.send(player, "Your target is not poisoned!");
                return false;
            } else {
                broadcastExecuteText(hero, target);
            }
            return true;
        }
        Messaging.send(player, "You must target a player!");
        return false;
    }

}
