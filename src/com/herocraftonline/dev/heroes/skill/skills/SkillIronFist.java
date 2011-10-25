package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillIronFist extends ActiveSkill {

    public SkillIronFist(Heroes plugin) {
        super(plugin, "IronFist");
        setDescription("Damages and knocks back nearby enemies");
        setUsage("/skill ironfist");
        setArgumentRange(0, 0);
        setIdentifiers("skill ironfist", "skill ifist");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 4);
        node.setProperty(Setting.RADIUS.node(), 3);
        node.setProperty("vertical-power", .25);
        node.setProperty("horizontal-power", .5);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        int radius = getSetting(hero, Setting.RADIUS.node(), 5, false);
        List<Entity> entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity target = (LivingEntity) entity;
            if (target.equals(player)) {
                continue;
            }

            // Check if the target is damagable
            if (!damageCheck(player, target)) {
                continue;
            }

            // Damage the target
            int damage = getSetting(hero, "damage", 1, false);
            addSpellTarget(target, hero);
            target.damage(damage, player);

            // Do our knockback
            Location playerLoc = player.getLocation();
            Location targetLoc = target.getLocation();
            
            double xDir =  targetLoc.getX() - playerLoc.getX();
            double zDir =  targetLoc.getZ() - playerLoc.getZ();
            double magnitude = Math.sqrt(xDir * xDir + zDir * zDir);
            double multiplier = this.getSetting(hero, "horizontal-power", .5, false);
            xDir = xDir / magnitude * multiplier;
            zDir = zDir / magnitude * multiplier;
            
            target.setVelocity(new Vector(xDir, getSetting(hero, "vertical-power", .25, false), zDir));
        }

        broadcastExecuteText(hero);
        return true;
    }
}
