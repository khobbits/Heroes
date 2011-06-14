package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillRevive extends ActiveSkill {
    public HashMap<Player, Location> deaths = new HashMap<Player, Location>();

    public SkillRevive(Heroes plugin) {
        super(plugin);
        name = "Revive";
        description = "Teleports the target to their place of death";
        usage = "/skill revive [target]";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("skill revive");
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillPlayerListener(), Priority.Normal);
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("You must target a player.");
            return false;
        }

        Player targetPlayer = (Player) target;
        if (deaths.containsKey(targetPlayer)) {
            Location loc = deaths.get(targetPlayer);
            double dx = player.getLocation().getX() - loc.getX();
            double dz = player.getLocation().getZ() - loc.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < 50) {
                if (targetPlayer.isDead()) {
                    player.sendMessage("That player is still dead");
                } else {
                    targetPlayer.teleport(loc);
                    notifyNearbyPlayers(player.getLocation(), useText, player.getName(), name, target == player ? "himself" : targetPlayer.getName());
                }
            }
        }
        return true;
    }
    
    public class SkillPlayerListener extends EntityListener {

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            if(event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Location playerLoc = player.getLocation();
                deaths.put(player, playerLoc);
            }
        }

    }
}
