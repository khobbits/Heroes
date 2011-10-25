package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillManaburn extends TargettedSkill {

    public SkillManaburn(Heroes plugin) {
        super(plugin, "Manaburn");
        setDescription("Burns the targets mana");
        setUsage("/skill manaburn");
        setArgumentRange(0, 1);
        setIdentifiers("skill manaburn", "skill mburn");
        setTypes(SkillType.DAMAGING, SkillType.SILENCABLE, SkillType.MANA, SkillType.HARMFUL);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("transfer-amount", 20);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            Messaging.send(player, "You need a target!");
            return false;
        }

        Hero tHero = plugin.getHeroManager().getHero((Player) target);

        int transferamount = getSetting(hero, "transfer-amount", 20, false);
        if (tHero.getMana() > transferamount) {
            if (hero.getMana() + transferamount > 100) {
                transferamount = 100 - hero.getMana();
            }
            tHero.setMana(tHero.getMana() - transferamount);
            broadcastExecuteText(hero, target);
            return true;
        } else {
            Messaging.send(player, "Target does not have enough mana!");
            return false;
        }
    }

}
