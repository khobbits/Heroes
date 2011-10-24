package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillScan extends TargettedSkill {

    private final Heroes plugin;
    
    public SkillScan(Heroes plugin) {
        super(plugin, "Scan");
        this.plugin = plugin;
        setDescription("Reports the target's health");
        setUsage("/skill scan <target>");
        setArgumentRange(1, 1);
        setIdentifiers("skill scan");
        setTypes(SkillType.KNOWLEDGE, SkillType.STEALTHY);
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        String name = "";
        int hp = 0;
        int maxHp = 0;
        if (target instanceof Player) {
            Hero tHero = plugin.getHeroManager().getHero((Player) target);
            hp = (int) tHero.getHealth();
            maxHp = (int) tHero.getMaxHealth();
            name = tHero.getPlayer().getDisplayName();
        } else if (target instanceof Creature){
            name = Messaging.getCreatureName((Creature) target);
            hp = target.getHealth();
            maxHp = plugin.getDamageManager().getCreatureHealth(Util.getCreatureFromEntity(target));
        } else {
            Messaging.send(player, "Invalid Target!");
            return false;
        }
        Messaging.send(player, "$1 has $2 / $3 HP", name, hp, maxHp);
        return true;
    }

}
