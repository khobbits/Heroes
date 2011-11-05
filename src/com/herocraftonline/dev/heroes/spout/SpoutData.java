package com.herocraftonline.dev.heroes.spout;

import java.util.HashMap;
import java.util.Map;

import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.herocraftonline.dev.heroes.Heroes;

public class SpoutData {

    private Map<String, Container> partyUIContainers;
    private final Heroes plugin;
    static final String config_ui_align = "TOP_LEFT";
    static final int config_ui_left = 3;
    static final int config_ui_top = 3;
    static final int config_ui_maxwidth = 160;
    
    public SpoutData(Heroes plugin) {
        this.plugin = plugin;
        partyUIContainers = new HashMap<String, Container>();
    }
    
    public void createPartyContainer(SpoutPlayer sPlayer) {
        Container container = SpoutUI.getContainer(sPlayer, config_ui_align, config_ui_left, config_ui_top, plugin);
        Container members = new GenericContainer();
        container.setLayout(ContainerType.HORIZONTAL).addChildren(members, new GenericContainer()).setWidth(config_ui_maxwidth);
        partyUIContainers.put(sPlayer.getName(), members);
    }
    
    public void removePartyContainer(SpoutPlayer sPlayer) {
        partyUIContainers.remove(sPlayer.getName());
    }
    
    public Container getPartyContainer(String name) {
        return partyUIContainers.get(name);
    }
}
