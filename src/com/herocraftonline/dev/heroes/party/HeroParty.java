package com.herocraftonline.dev.heroes.party;

import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroParty {
    private Player leader;
    private List<Player> members;
    private List<String> modes;
    private List<Player> invites;
    
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
        for(Player p : members) {
            if(p == player) {
                members.remove(player);
            }
        }
    }
    
    public void addMember(Player player) {
        for(Player p : members) {
            if(p == player) {
                return;
            }
        }
        members.add(player);
    }
    
    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public void addInvite(Player player) {
        for(Player p : invites) {
            if(p == player) {
                return;
            }
        }
        invites.add(player);
    }
    
    public void removeInvite(Player player) {
        for(Player p : invites) {
            if(p == player) {
                invites.remove(player);
            }
        }
    }
    
    public boolean isInvited(Player player) {
        return invites.contains(player);
    }
    
    public void addMode(String mode) {
        modes.add(mode);
    }
    
    public void removeMode(String mode) {
        modes.remove(mode);
    }
    
    public boolean checkMode(String mode) {
        return modes.contains(mode);
    }
    
    public void messageParty(String msg, String... params) {
        for(Player p : members) {
            Messaging.send(p, msg, params);
        }
    }
    
}
