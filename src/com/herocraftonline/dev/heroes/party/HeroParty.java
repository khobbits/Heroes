package com.herocraftonline.dev.heroes.party;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.util.Messaging;

public class HeroParty {
    private Player leader;
    private Set<Player> members = new HashSet<Player>();
    private Set<String> modes = new HashSet<String>();
    private Set<String> invites = new HashSet<String>();

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

    public boolean isInvited(String player) {
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

    public void messageParty(String msg, Object... params) {
        for (Player p : members) {
            Messaging.send(p, msg, params);
        }
    }

}
