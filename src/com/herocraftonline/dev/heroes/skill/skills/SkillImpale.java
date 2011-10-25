package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.common.SlowEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillImpale extends TargettedSkill {
    
    private String applyText;
    private String expireText;
    
    
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
        node.setProperty(Setting.DURATION.node(), 5000);
        node.setProperty("amplitude", 4);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% has been slowed by %hero%'s impale!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% is no longer slowed!");
        node.setProperty("force", 3);
        return node;
    }
    
    @Override
    public void init() {
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% has been slowed by %hero%'s impale!").replace("%target%", "$1").replace("%hero%", "$2");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% is no longer slowed!").replace("%target%", "$1");
    }
    
    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        Material item = player.getItemInHand().getType();
        if (!getSetting(hero, "weapons", Util.swords).contains(item.name())) {
            Messaging.send(player, "You can't use impale with that weapon!");
        }

        HeroClass heroClass = hero.getHeroClass();
        int force = getSetting(hero, "force", 3, false);
        int damage = heroClass.getItemDamage(item) == null ? 0 : heroClass.getItemDamage(item);
        target.damage(damage, player);
        //Do a little knockup
        target.setVelocity(target.getVelocity().add(new Vector(0, force, 0)));
        //Add the slow effect
        long duration = getSetting(hero, Setting.DURATION.node(), 5000, false);
        int amplitude = getSetting(hero, "amplitude", 4, false);
        SlowEffect sEffect = new SlowEffect(this, duration, amplitude, false, applyText, applyText, hero);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(sEffect);
        } else if (target instanceof Creature) {
            plugin.getEffectManager().addCreatureEffect((Creature) target, sEffect);
        }
        
        broadcastExecuteText(hero, target);
        return true;
    }
}
