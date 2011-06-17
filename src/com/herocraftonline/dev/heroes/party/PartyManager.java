package com.herocraftonline.dev.heroes.party;

import java.util.HashSet;
import java.util.Set;

import com.herocraftonline.dev.heroes.Heroes;

@SuppressWarnings("unused")
public class PartyManager {

    private Heroes plugin;
    private Set<HeroParty> parties = new HashSet<HeroParty>();

    public PartyManager(Heroes plugin) {
        this.plugin = plugin;
    }

    public void addParty(HeroParty party) {
        parties.add(party);
    }

    public void removeParty(HeroParty party) {
        parties.remove(party);
    }

}
