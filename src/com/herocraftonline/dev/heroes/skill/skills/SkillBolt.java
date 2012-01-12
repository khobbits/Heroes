package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBolt extends TargettedSkill {

    public SkillBolt(Heroes plugin) {
        super(plugin, "Bolt");
        setDescription("Calls a bolt of lightning down on the target dealing $1 damage.");
        setUsage("/skill bolt <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill bolt");
        setTypes(SkillType.LIGHTNING, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DAMAGE.node(), 4);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        target.getWorld().strikeLightningEffect(target.getLocation());
        int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 4, false);
        
        plugin.getDamageManager().addSpellTarget(target, hero, this);
        damageEntity(target, player, damage, DamageCause.ENTITY_ATTACK);

        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 4, false);
        return getDescription().replace("$1", damage + "");
    }
}
