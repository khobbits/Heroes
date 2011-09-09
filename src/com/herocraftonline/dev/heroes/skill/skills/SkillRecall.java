package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillRecall extends ActiveSkill {

    public SkillRecall(Heroes plugin) {
        super(plugin, "Recall");
        setDescription("Recalls you to your marked Location");
        setUsage("/skill recall");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill recall" });
        
        setTypes(SkillType.SILENCABLE, SkillType.TELEPORT);
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Map<String, String> skillSetting = hero.getSkillSettings(this);

        // Try to teleport back to the location
        World world = validateLocation(skillSetting, player);
        if (world == null)
            return false;
        
        double[] xyzyp = getStoredData(skillSetting);
        broadcastExecuteText(hero);
        player.teleport(new Location(world, xyzyp[0], xyzyp[1], xyzyp[2], (float) xyzyp[3], (float) xyzyp[4]));
        return true;
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
        World world = plugin.getServer().getWorld((String) skillSetting.get("world"));
        if (world == null) {
            Messaging.send(player, "You have an invalid recall location marked!");
            return null;
        }

        return world;
    }
}
