package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillStealEssence extends TargettedSkill {

    public SkillStealEssence(Heroes plugin) {
        super(plugin, "StealEssence");
        setDescription("Steals a beneficial effect from the target");
        setUsage("/skill stealessence");
        setArgumentRange(0, 1);
        setIdentifiers("skill stealessence", "skill sessence");
        setTypes(SkillType.DEBUFF, SkillType.KNOWLEDGE, SkillType.BUFF, SkillType.HARMFUL, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% used %skill% and stole %effect% from %target%!");
        node.setProperty(Setting.AMOUNT.node(), 3);
        return node;
    }

    @Override
    public void init() {
        super.init();
        this.setUseText(getSetting(null, Setting.APPLY_TEXT.node(), "%hero% used %skill% and stole %effect%from %target%!").replace("%hero%", "$1").replace("%skill%", "$2").replace("%effect%", "$3").replace("%target%", "$4"));
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        if (!(target instanceof Player)) {
            Messaging.send(player, "Invalid Target!");
            return false;
        }

        ArrayList<Effect> possibleEffects = new ArrayList<Effect>();
        Hero tHero = getPlugin().getHeroManager().getHero((Player) target);
        for (Effect e : tHero.getEffects()) {
            if (e.isType(EffectType.BENEFICIAL) && e.isType(EffectType.DISPELLABLE)) {
                possibleEffects.add(e);
            }
        }

        if (possibleEffects.isEmpty()) {
            Messaging.send(player, "That target has no effects to steal!");
            return false;
        }

        String stolenNames = "";
        int numEffects = getSetting(hero, Setting.AMOUNT.node(), 3, false);
        for (int i = 0; i < numEffects && possibleEffects.size() > 0; i++) {
            Effect stolenEffect = possibleEffects.get(Util.rand.nextInt(possibleEffects.size()));
            tHero.removeEffect(stolenEffect);
            hero.addEffect(stolenEffect);
            possibleEffects.remove(stolenEffect);
            stolenNames += stolenEffect.getName() + " ";
        }

        broadcast(player.getLocation(), getUseText(), player.getDisplayName(), getName(), stolenNames, tHero.getPlayer().getDisplayName());
        return true;
    }

}
