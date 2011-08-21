package com.herocraftonline.dev.heroes.party;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroParty {

    private Hero leader;
    private Set<Hero> members = new HashSet<Hero>();
    private Boolean pvp = true;
    private Boolean exp = true;
    private LinkedList<String> invites = new LinkedList<String>();
    private boolean updateMapDisplay = true;

    public HeroParty(Hero leader) {
        this.leader = leader;
        members.add(leader);
    }

    public void addInvite(String player) {
        invites.add(player);
    }

    public void addMember(Hero hero) {
        setUpdateMapDisplay(true);
        members.add(hero);
    }

    public void expToggle() {
        if (exp == true) {
            exp = false;
            messageParty("ExpShare is now disabled!");
        } else {
            exp = true;
            messageParty("ExpShare is now enabled!");
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

    public Boolean getPvp() {
        return pvp;
    }

    public boolean isInvited(String player) {
        return invites.contains(player);
    }

    public boolean isPartyMember(Hero hero) {
        return members.contains(hero);
    }

    public void messageParty(String msg, Object... params) {
        for (Hero hero : members) {
            Messaging.send(hero.getPlayer(), msg, params);
        }
    }

    public void pvpToggle() {
        if (pvp == true) {
            pvp = false;
            messageParty("PvP is now enabled!");
        } else {
            pvp = true;
            messageParty("PvP is now disabled!");
        }
    }

    public void removeInvite(Player player) {
        invites.remove(player);
    }

    public void removeMember(Hero hero) {
        setUpdateMapDisplay(true);
        members.remove(hero);
        hero.setParty(null);
        if (members.size() == 1) {
            Hero remainingMember = members.iterator().next();
            remainingMember.setParty(null);
            messageParty("Party disbanded.");
            members.remove(remainingMember);
            return;
        }
        if (hero.equals(leader) && !members.isEmpty()) {
            leader = members.iterator().next();
            messageParty("$1 is now leading the party.", leader.getPlayer().getDisplayName());
        }
    }

    public void removeOldestInvite() {
        invites.pop();
    }

    public void setLeader(Hero leader) {
        this.leader = leader;
    }

    public boolean updateMapDisplay() {
        return updateMapDisplay;
    }

    public void setUpdateMapDisplay(boolean updateMapDisplay) {
        this.updateMapDisplay = updateMapDisplay;
    }

}
