package com.herocraftonline.dev.heroes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.command.skill.OutsourcedSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.persistence.HeroManager;

public class HPlayerListener extends PlayerListener {
    private final Heroes plugin;

    public HPlayerListener(Heroes instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HeroManager heroManager = plugin.getHeroManager();
        heroManager.saveHeroFile(player);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        long start = System.currentTimeMillis();
        Player player = event.getPlayer();
        HeroManager heroManager = plugin.getHeroManager();
        heroManager.loadHeroFile(player);
        plugin.swapNetServerHandler(player);
        plugin.inventoryCheck(player);
        System.out.println("Player join time: " + (System.currentTimeMillis() - start));
    }

    @Override
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        plugin.inventoryCheck(event.getPlayer());
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        plugin.inventoryCheck(event.getPlayer());
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        Player player = event.getPlayer();
        Material material = player.getItemInHand().getType();
        Hero hero = plugin.getHeroManager().getHero(player);
        if (hero.getBinds().containsKey(material)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                String[] args = hero.getBinds().get(material);
                plugin.onCommand(player, null, "skill", args);
            }
        }
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        Player player = event.getPlayer();
        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            Hero hero = plugin.getHeroManager().getHero(player);
            HeroClass heroClass = hero.getHeroClass();
            
            List<BaseCommand> commands = plugin.getCommandManager().getCommands();
            for (BaseCommand cmd : commands) {
                if (cmd instanceof OutsourcedSkill) {
                    OutsourcedSkill skill = (OutsourcedSkill) cmd;
                    if (heroClass.hasSkill(skill.getName())) {
                        skill.tryLearningSkill(hero);
                    }
                }
            }
        }

    }
}
