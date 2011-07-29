package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillReplenish extends ActiveSkill {

    public SkillReplenish(Heroes plugin) {
        super(plugin, "Replenish");
        setDescription("Brings your mana back to full");
        setUsage("/skill replenish");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill replenish"});
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        hero.setMana(100);
        Messaging.send(hero.getPlayer(), "Your mana has been fully replenished!");
        if (hero.isVerbose()) {
            Messaging.send(hero.getPlayer(), Messaging.createManaBar(100));
        }
        broadcastExecuteText(hero);
        return true;
    }

}
