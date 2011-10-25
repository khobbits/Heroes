package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillBattery extends TargettedSkill {

    public SkillBattery(Heroes plugin) {
        super(plugin, "Battery");
        setDescription("Gives your target mana");
        setUsage("/skill battery");
        setArgumentRange(0, 1);
        setTypes(SkillType.SILENCABLE, SkillType.MANA);
        setIdentifiers("skill battery");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("transfer-amount", 20);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (!(target instanceof Player))
            return false;

        Hero tHero = plugin.getHeroManager().getHero((Player) target);
        if (tHero == null)
            return false;

        if (tHero.equals(hero))
            return false;

        int transferAmount = getSetting(hero, "transfer-amount", 20, false);
        if (hero.getMana() > transferAmount) {
            if (tHero.getMana() + transferAmount > 100) {
                transferAmount = 100 - tHero.getMana();
            }
            hero.setMana(hero.getMana() - transferAmount);
            tHero.setMana(tHero.getMana() + transferAmount);
            broadcastExecuteText(hero, target);
            return true;
        } else {
            Messaging.send(hero.getPlayer(), "You need at least $1 mana to transfer.", transferAmount);
            return false;
        }
    }

}
