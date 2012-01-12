package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
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
        Map<String, String> skillSettings = hero.getSkillSettings(this);

        // Try to teleport back to the location
        World world = validateLocation(skillSettings, player);
        if (world == null)
            return SkillResult.FAIL;

        if (hero.hasEffectType(EffectType.ROOT)) {
            Messaging.send(player, "Teleport fizzled.");
            return SkillResult.FAIL;
        }

        double[] xyzyp = getStoredData(skillSettings);
        broadcastExecuteText(hero);
        player.teleport(new Location(world, xyzyp[0], xyzyp[1], xyzyp[2], (float) xyzyp[3], (float) xyzyp[4]));
        return SkillResult.NORMAL;
    }

    private double[] getStoredData(Map<String, String> skillSettings) {
        double[] xyzyp = new double[5];

        xyzyp[0] = Double.valueOf(skillSettings.get("x"));
        xyzyp[1] = Double.valueOf(skillSettings.get("y"));
        xyzyp[2] = Double.valueOf(skillSettings.get("z"));
        xyzyp[3] = Double.valueOf(skillSettings.get("yaw"));
        xyzyp[4] = Double.valueOf(skillSettings.get("pitch"));

        return xyzyp;
    }

    private World validateLocation(Map<String, String> skillSetting, Player player) {
        if (skillSetting == null) {
            Messaging.send(player, "You do not have a recall location marked.");
            return null;
        }

        // Make sure the world setting isn't null - this lets us know the player has a location saved
        if (skillSetting.get("world") == null || skillSetting.get("world").equals("")) {
            Messaging.send(player, "You do not have a recall location marked.");
            return null;
        }
        // Get the world and make sure it's still available to return to
        World world = plugin.getServer().getWorld(skillSetting.get("world"));
        if (world == null) {
            Messaging.send(player, "You have an invalid recall location marked!");
            return null;
        }

        return world;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }
}
