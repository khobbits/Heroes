package com.herocraftonline.dev.heroes.party;

import java.util.List;

import com.herocraftonline.dev.heroes.Heroes;

public class PartyManager {

    private Heroes plugin;
    private List<HeroParty> parties;
    
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
