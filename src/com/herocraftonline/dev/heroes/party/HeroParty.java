package com.herocraftonline.dev.heroes.party;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;
import com.herocraftonline.dev.heroes.util.Util;

public class HeroParty {

    private Hero leader;
    private Set<Hero> members = new HashSet<Hero>();
    private Boolean noPvp = true;
    private Boolean exp = true;
    private final Heroes plugin;
    private LinkedList<String> invites = new LinkedList<String>();

    public HeroParty(Hero leader, Heroes plugin) {
        this.plugin = plugin;
        this.leader = leader;
        members.add(leader);
    }

    public void addInvite(String player) {
        invites.add(player);
    }

    public void addMember(Hero hero) {
        members.add(hero);
        update();
    }

    public void expToggle() {
        if (exp == true) {
            exp = false;
            messageParty("Exp sharing is now disabled!");
        } else {
            exp = true;
            messageParty("Exp sharing is now enabled!");
        }
    }

    public Boolean getExp() {
        return exp;
    }

    public int getInviteCount() {
        return invites.size();
    }

    public Hero getLeader() {
        return leader;
    }

    public Set<Hero> getMembers() {
        return new HashSet<Hero>(members);
    }

    public boolean isInvited(String player) {
        return invites.contains(player);
    }

    public Boolean isNoPvp() {
        return noPvp;
    }

    public boolean isPartyMember(Hero hero) {
        return members.contains(hero);
    }

    public boolean isPartyMember(Player player) {
        for (Hero hero : members) {
            if (hero.getPlayer().equals(player))
                return true;
        }
        return false;
    }

    public void messageParty(String msg, Object... params) {
        for (Hero hero : members) {
            Messaging.send(hero.getPlayer(), msg, params);
        }
    }

    public void pvpToggle() {
        if (noPvp == true) {
            noPvp = false;
            messageParty("PvP is now enabled!");
        } else {
            noPvp = true;
            messageParty("PvP is now disabled!");
        }
    }

    public void removeInvite(Player player) {
        invites.remove(player);
    }

    public void removeMember(Hero hero) {
        members.remove(hero);
        hero.setParty(null);
        if (members.size() == 1) {
            Hero remainingMember = members.iterator().next();
            remainingMember.setParty(null);
            messageParty("Party disbanded.");
            members.remove(remainingMember);
            leader = null;
            return;
        }
        if (hero.equals(leader) && !members.isEmpty()) {
            leader = members.iterator().next();
            messageParty("$1 is now leading the party.", leader.getPlayer().getDisplayName());
        }
        update();
    }

    public void removeOldestInvite() {
        invites.pop();
    }

    public void setLeader(Hero leader) {
        this.leader = leader;
    }

    public void update() {
        if (Heroes.useSpout()) 
            for (Hero hero : members)
                plugin.getSpoutData().updatePartyDisplay(hero.getPlayer(), this);   
    }

    public void gainExp(double amount, ExperienceType type, Location location) {
        Set<Hero> inRangeMembers = new HashSet<Hero>();
        for (Hero partyMember : members) {
            if (!location.getWorld().equals(partyMember.getPlayer().getLocation().getWorld()))
                continue;

            if (location.distanceSquared(partyMember.getPlayer().getLocation()) > 2500)
                continue;

            if (partyMember.canGain(type))
                inRangeMembers.add(partyMember);
        }

        int partySize = inRangeMembers.size();
        double sharedExp = amount / partySize;
        double bonusExp = partySize > 1 ? sharedExp : 0;
        if (partySize > 1)
            bonusExp *= Properties.partyMults[partySize - 1];

        bonusExp *= Heroes.properties.partyBonus;
        bonusExp = Util.formatDouble(bonusExp);

        for (Hero partyMember : inRangeMembers) {
            partyMember.gainExp(sharedExp + bonusExp, type);
        }
        return;
    }
}
