package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillRecall extends ActiveSkill {

    public SkillRecall(Heroes plugin) {
        super(plugin, "Recall");
        setDescription("You recall to your marked location.");
        setUsage("/skill recall");
        setArgumentRange(0, 0);
        setIdentifiers("skill recall");
        setTypes(SkillType.SILENCABLE, SkillType.TELEPORT);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        ConfigurationSection skillSettings = hero.getSkillSettings(this);

        // Try to teleport back to the location
        World world = SkillMark.validateLocation(skillSettings, player);
        if (world == null)
            return SkillResult.FAIL;

        if (hero.hasEffectType(EffectType.ROOT)) {
            Messaging.send(player, "Teleport fizzled.");
            return SkillResult.FAIL;
        }

        double[] xyzyp = null;
        try {
            xyzyp = SkillMark.getStoredData(skillSettings);
        } catch (IllegalArgumentException e) {
            Messaging.send(player, "Your recall location is improperly set!");
            return SkillResult.SKIP_POST_USAGE;
        }
        broadcastExecuteText(hero);
        player.teleport(new Location(world, xyzyp[0], xyzyp[1], xyzyp[2], (float) xyzyp[3], (float) xyzyp[4]));
        return SkillResult.NORMAL;
    }


    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
