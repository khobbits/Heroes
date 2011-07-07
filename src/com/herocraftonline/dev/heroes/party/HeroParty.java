package com.herocraftonline.dev.heroes.party;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroParty {    
    private Player leader;
    private Set<Player> members = new HashSet<Player>();
    private Boolean pvp = true;
    private Boolean exp = true;
    private LinkedList<String> invites = new LinkedList<String>();

    public HeroParty(Player leader) {
        this.leader = leader;
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = leader;
    }

    public boolean isPartyMember(Player player) {
        return members.contains(player);
    }

    public void removeMember(Player player) {
        members.remove(player);
        if (player.equals(leader) && !members.isEmpty()) {
            leader = members.iterator().next();
            messageParty("$1 is now leading the party.", leader.getDisplayName());
        }
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public Set<Player> getMembers() {
        return members;
    }

    public void addInvite(String player) {
        invites.add(player);

    }

    public void removeInvite(Player player) {
        invites.remove(player);

    }
    
    public void removeOldestInvite() {
        invites.pop();
    }

    public boolean isInvited(String player) {
        return invites.contains(player);
    }
    
    public int getInviteCount() {
        return invites.size();
    }


    public void pvpToggle() {
        if(pvp == true) {
            pvp = false;
            messageParty("PvP is now enabled!");
        }else {
            pvp = true;
            messageParty("PvP is now disabled!");
        }
    }
    
    public void expToggle() {
        if(exp == true) {
            exp = false;
            messageParty("ExpShare is now disabled!");
        }else {
            exp = true;
            messageParty("ExpShare is now enabled!");
        }
    }

    public Boolean getPvp() {
        return pvp;
    }

    public Boolean getExp() {
        return exp;
    }

    public void messageParty(String msg, Object... params) {
        for (Player p : members) {
            Messaging.send(p, msg, params);
        }
    }

}
