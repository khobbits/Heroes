package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public final class SkillManaScroll extends ActiveSkill{

    public SkillManaScroll(Heroes plugin, String name) {
        super(plugin, "ManaScroll");
        setDescription("Enchants a map into a mana regenerating scroll");
        setUsage("/skill manascroll");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill manascroll" });
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

}
