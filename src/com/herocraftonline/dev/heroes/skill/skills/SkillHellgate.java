package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillHellgate extends ActiveSkill {

    public SkillHellgate(Heroes plugin) {
        super(plugin);
        setName("Hellgate");
        setDescription("Teleports you and your nearby party to or from the nether - 5 bones");
        setUsage("/skill hellgate");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill hellgate");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        String playerName = player.getName();

        List<Entity> entityList = player.getNearbyEntities(10, 10, 10);
        for (Entity n : entityList) {
            if (n instanceof Player) {
                Player nPlayer = (Player) n;
                Hero nHero = plugin.getHeroManager().getHero(nPlayer);
                if (nHero.getParty() == hero.getParty()) {
                    List<World> worlds = plugin.getServer().getWorlds();
                    for (World w : worlds) {
                        if (w.getEnvironment() == Environment.NETHER) {
                            nPlayer.teleport(new Location(w, nPlayer.getLocation().getX(), nPlayer.getLocation().getY(), nPlayer.getLocation().getZ()));
                        }
                    }
                }
            }
        }
        broadcast(player.getLocation(), getUseText(), playerName, getName());
        return true;
    }

}
