package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillMark extends ActiveSkill {

    public SkillMark(Heroes plugin) {
        super(plugin, "Mark");
        setDescription("Marks a location for use with recall");
        setUsage("/skill mark <info>");
        setArgumentRange(0, 1);
        setIdentifiers("skill mark");
        setTypes(SkillType.TELEPORT);
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Map<String, String> skillSetting = hero.getSkillSettings("Recall");

        if (args.length > 0) {
            // Display the info about the current mark
            World world = validateLocation(skillSetting, player);
            if (world == null)
                return SkillResult.FAIL;
            double[] xyzyp = getStoredData(skillSetting);
            Messaging.send(player, "Your recall is currently marked on $1 at: $2, $3, $4", world.getName(), (int) xyzyp[0], (int) xyzyp[1], (int) xyzyp[2]);
            return SkillResult.SKIP_POST_USAGE;
        } else {
            // Save a new mark
            Location loc = player.getLocation();
            hero.setSkillSetting("Recall", "world", loc.getWorld().getName());
            hero.setSkillSetting("Recall", "x", loc.getX());
            hero.setSkillSetting("Recall", "y", loc.getY());
            hero.setSkillSetting("Recall", "z", loc.getZ());
            hero.setSkillSetting("Recall", "yaw", (double) loc.getYaw());
            hero.setSkillSetting("Recall", "pitch", (double) loc.getPitch());
            Object[] obj = new Object[] { loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() };
            Messaging.send(player, "You have marked a new location on $1 at: $2, $3, $4", obj);

            plugin.getHeroManager().saveHero(hero);
            return SkillResult.NORMAL;
        }
    }

    private double[] getStoredData(Map<String, String> skillSetting) {
        double[] xyzyp = new double[5];

        xyzyp[0] = Double.valueOf(skillSetting.get("x"));
        xyzyp[1] = Double.valueOf(skillSetting.get("y"));
        xyzyp[2] = Double.valueOf(skillSetting.get("z"));
        xyzyp[3] = Double.valueOf(skillSetting.get("yaw"));
        xyzyp[4] = Double.valueOf(skillSetting.get("pitch"));

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
}
