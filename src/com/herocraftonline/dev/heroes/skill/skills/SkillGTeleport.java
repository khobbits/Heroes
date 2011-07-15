package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillGTeleport extends ActiveSkill {

    public SkillGTeleport(Heroes plugin) {
        super(plugin);
        setName("GroupTeleport");
        setDescription("Summons your group to your location");
        setUsage("/skill groupteleport");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill groupteleport");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        if (hero.getParty() != null && hero.getParty().getMembers().size() != 1) {
            Player player = hero.getPlayer();
            String heroName = player.getName();
            for (Hero partyMember : hero.getParty().getMembers()) {
                partyMember.getPlayer().teleport(player);
            }
            broadcast(player.getLocation(), getUseText(), heroName, getName());
            return true;
        }
        return false;
    }
}
