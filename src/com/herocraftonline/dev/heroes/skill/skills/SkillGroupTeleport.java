package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillGroupTeleport extends ActiveSkill {

    public SkillGroupTeleport(Heroes plugin) {
        super(plugin, "GroupTeleport");
        setDescription("Summons your group to your location");
        setUsage("/skill groupteleport");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill groupteleport" });
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        if (hero.getParty() != null && hero.getParty().getMembers().size() != 1) {
            Player player = hero.getPlayer();
            for (Hero partyMember : hero.getParty().getMembers()) {
                partyMember.getPlayer().teleport(player);
            }
            broadcastExecuteText(hero);
            return true;
        }
        return false;
    }
}
