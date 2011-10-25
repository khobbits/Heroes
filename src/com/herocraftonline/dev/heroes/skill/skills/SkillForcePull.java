package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillForcePull extends TargettedSkill {

    public SkillForcePull(Heroes plugin) {
        super(plugin, "Forcepull");
        setDescription("Forces your target toward you");
        setUsage("/skill forcepull <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill forcepull", "skill fpull");
        setTypes(SkillType.FORCE, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 0);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        int damage = getSetting(hero, Setting.DAMAGE.node(), 0, false);
        if (damage > 0) {
            addSpellTarget(target, hero);
            target.damage(damage, player);
        }        
        
        Location playerLoc = player.getLocation();
        Location targetLoc = target.getLocation();
        
        double distance = player.getLocation().distanceSquared(target.getLocation());
        double xDir = playerLoc.getX() - targetLoc.getX();
        double zDir = playerLoc.getZ() - targetLoc.getZ();
        double magnitude = Math.sqrt(xDir * xDir + zDir * zDir);
        double multiplier = Math.sqrt(distance) / 8;
        xDir = xDir / magnitude * multiplier;
        zDir = zDir / magnitude * multiplier;
        
        target.setVelocity(new Vector(xDir, 1, zDir));

        broadcastExecuteText(hero, target);
        return true;
    }

}