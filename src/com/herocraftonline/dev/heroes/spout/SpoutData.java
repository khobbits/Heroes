package com.herocraftonline.dev.heroes.spout;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.spout.gui.EntityBar;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public void updatePartyDisplay(Player player, HeroParty party) {
        SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
        if (!sPlayer.isSpoutCraftEnabled()) {
            return;
        }

        Container container = plugin.getSpoutData().getPartyContainer(player.getName());
        if (container == null) {
            return;
        }

        int index = 0;
        Set<Hero> heroes = party.getMembers();
        Hero hero = plugin.getHeroManager().getHero(player);
        index = updateContainer(hero, container, index);
        heroes.remove(hero);
        for (Hero h : heroes) {
            index = updateContainer(h, container, index);
        }
        Widget[] bars = container.getChildren();
        while (index < bars.length) {
            container.removeChild(bars[index++]);
        }

        container.updateLayout();
    }

    public int updateContainer(Hero hero, Container container, int index) {
        EntityBar bar;
        if (index >= container.getChildren().length) {
            container.addChild(bar = new EntityBar(plugin));
        } else {
            bar = (EntityBar) container.getChildren()[index];
        }
        bar.setEntity(hero.getPlayer().getName(), hero.getParty().getLeader().equals(hero) ? ChatColor.GREEN + "@" : "");
        Set<LivingEntity> summons = hero.getSummons();
        bar.setTargets(plugin, summons.isEmpty() ? null : summons.toArray(new LivingEntity[summons.size()]));
        return ++index;
    }
}
