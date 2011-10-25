package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillMegabolt extends TargettedSkill {

    public SkillMegabolt(Heroes plugin) {
        super(plugin, "Megabolt");
        setDescription("Calls down multiple bolts of lightning centered on the target.");
        setUsage("/skill mbolt <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill megabolt", "skill mbolt");
        setTypes(SkillType.LIGHTNING, SkillType.DAMAGING, SkillType.SILENCABLE, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DAMAGE.node(), 4);
        node.setProperty(Setting.RADIUS.node(), 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        int range = getSetting(hero, Setting.RADIUS.node(), 5, false);
        int damage = getSetting(hero, Setting.DAMAGE.node(), 4, false);

        // Damage the first target
        addSpellTarget(target, hero);
        target.getWorld().strikeLightningEffect(target.getLocation());
        target.damage(damage, player);

        for (Entity entity : target.getNearbyEntities(range, range, range)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                // Check if the target is damagable
                if (!damageCheck(player, (LivingEntity) entity)) {
                    continue;
                }

                addSpellTarget(entity, hero);
                entity.getWorld().strikeLightningEffect(entity.getLocation());
                ((LivingEntity) entity).damage(damage, player);
            }
        }

        broadcastExecuteText(hero, target);
        return true;
    }

}
