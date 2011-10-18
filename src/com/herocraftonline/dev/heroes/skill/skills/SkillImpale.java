package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.herocraftonline.util.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillImpale extends TargettedSkill {

    public SkillImpale(Heroes plugin) {
        super(plugin, "Impale");
        setDescription("You attempt to impale your target on your weapon, tossing them up in the air momentarily.");
        setUsage("/skill impale <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill impale");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.HARMFUL, SkillType.FORCE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("weapons", Util.shovels);
        node.setProperty(Setting.MAX_DISTANCE.node(), 6);
        node.setProperty("force", 3);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        Material item = player.getItemInHand().getType();
        if (!getSetting(hero.getHeroClass(), "weapons", Util.swords).contains(item.name())) {
            Messaging.send(player, "You can't use impale with that weapon!");
        }

        HeroClass heroClass = hero.getHeroClass();
        int force = getSetting(heroClass, "force", 3);
        int damage = heroClass.getItemDamage(item) == null ? 0 : heroClass.getItemDamage(item);
        target.damage(damage, player);
        target.setVelocity(target.getVelocity().add(new Vector(0, force, 0)));
        broadcastExecuteText(hero, target);
        return true;
    }
}
