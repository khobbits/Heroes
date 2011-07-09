package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillTrack extends ActiveSkill {

    private static final Random random = new Random();

    public SkillTrack(Heroes plugin) {
        super(plugin);
        setName("Track");
        setDescription("Locates a player");
        setUsage("/skill track <player>");
        setMinArgs(1);
        setMaxArgs(1);
        getIdentifiers().add("skill track");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("randomness", 50);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            Messaging.send(player, "Target not found.");
            return false;
        }

        HeroClass heroClass = plugin.getHeroManager().getHero(player).getHeroClass();

        Location location = target.getLocation();
        int randomness = getSetting(heroClass, "randomness", 50);
        int x = location.getBlockX() + random.nextInt(randomness);
        int y = location.getBlockY() + random.nextInt(randomness / 10);
        int z = location.getBlockZ() + random.nextInt(randomness);

        Messaging.send(player, "Tracked $1: $2,$3,$4", target.getName(), x, y, z);
        player.setCompassTarget(location);
        notifyNearbyPlayers(player.getLocation(), getUseText(), player.getName(), getName());
        return true;
    }

}
