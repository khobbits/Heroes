package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SkillGroupTeleport extends ActiveSkill {

    public SkillGroupTeleport(Heroes plugin) {
        super(plugin, "GroupTeleport");
        setDescription("Summons your group to your location");
        setUsage("/skill groupteleport");
        setArgumentRange(0, 0);
        setIdentifiers("skill groupteleport", "skill gteleport");
        setTypes(SkillType.TELEPORT, SkillType.SILENCABLE);
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
