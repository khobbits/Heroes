package com.herocraftonline.dev.heroes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.OutsourcedSkill;
import com.nijiko.permissions.StorageReloadEvent;
import com.nijiko.permissions.WorldConfigLoadEvent;

public class HPermissionsListener extends CustomEventListener {

    private Heroes plugin;

    public HPermissionsListener(Heroes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCustomEvent(Event event) {
        if (Heroes.Permissions != null) {
            if (event instanceof StorageReloadEvent) {
                relearnSkills();
            } else if (event instanceof WorldConfigLoadEvent) {
                World world = Bukkit.getServer().getWorld(((WorldConfigLoadEvent) event).getWorld());
                if (world != null) {
                    relearnSkills(world);
                }
            }
        }
    }

    /**
     * The following attempts to relearn all skills for all players on the server.
     */
    private void relearnSkills() {
        // Cycle through all the Worlds on the server and relearn the Skills of Players in those Worlds.
        for (World world : Bukkit.getServer().getWorlds()) {
            relearnSkills(world);
        }
    }

    /**
     * The following attempts to relearn all skills for the players in a given world.
     * 
     * @param world
     */
    private void relearnSkills(World world) {
        List<Player> players = new ArrayList<Player>(world.getPlayers());
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            // Grab Hero.
            Hero hero = this.plugin.getHeroManager().getHero(player);
            // Grab Commands so we can parse them for Skills, seeing as Skills are still set as Commands.
            List<BaseCommand> sortCommands = plugin.getCommandManager().getCommands();
            for (BaseCommand command : sortCommands) {
                // We're only interested in Permission based Skills.
                if (command instanceof OutsourcedSkill) {
                    ((OutsourcedSkill) command).tryLearningSkill(hero);
                }
            }
        }
    }
}
