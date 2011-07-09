package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillManaburn extends TargettedSkill {

    public SkillManaburn(Heroes plugin) {
        super(plugin);
        setName("Manaburn");
        setDescription("Burns the targets mana");
        setUsage("/skill manaburn");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill manaburn");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("transfer-amount", 20);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (!(target instanceof Player)) {
            return false;
        }
        Hero tHero = plugin.getHeroManager().getHero((Player) target);
        if (tHero == null) {
            return false;
        }
        int transferamount = getSetting(hero.getHeroClass(), "transfer-amount", 20);
        if (tHero.getMana() > transferamount) {
            if ((hero.getMana() + transferamount) > 100) {
                transferamount = (100 - hero.getMana());
            }
            tHero.setMana(tHero.getMana() - transferamount);
            notifyNearbyPlayers(hero.getPlayer().getLocation(), getUseText(), hero.getPlayer().getName(), getName());
            return true;
        } else {
            return false;
        }
    }

}
