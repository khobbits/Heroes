package com.herocraftonline.dev.heroes.effects.common;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class RootEffect extends PeriodicExpirableEffect {

    private static final long period = 100;
    private final String applyText = "$1 was rooted!";
    private final String expireText = "Root faded from $1!";

    private double x, y, z;

    public RootEffect(Skill skill, long duration) {
        super(skill, "Root", period, duration);
        this.types.add(EffectType.DISPELLABLE);
        this.types.add(EffectType.ROOT);
        this.types.add(EffectType.HARMFUL);
    }

    @Override
    public void apply(Creature creature) {
        super.apply(creature);
        Location location = creature.getLocation();
        x = location.getX();
        y = location.getY();
        z = location.getZ();

        broadcast(location, applyText, Messaging.getCreatureName(creature));
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);

        Location location = hero.getPlayer().getLocation();
        x = location.getX();
        y = location.getY();
        z = location.getZ();

        Player player = hero.getPlayer();
        broadcast(location, applyText, player.getDisplayName());
    }

    @Override
    public void remove(Creature creature) {
        super.remove(creature);
        broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature));
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), expireText, player.getDisplayName());
    }

    @Override
    public void tick(Creature creature) {
        super.tick(creature);
        
        Location location = creature.getLocation();
        if (location.getX() != x || location.getY() != y || location.getZ() != z) {
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            location.setYaw(creature.getLocation().getYaw());
            location.setPitch(creature.getLocation().getPitch());
            creature.teleport(location);
        }
    }
    
    @Override
    public void tick(Hero hero) {
        super.tick(hero);

        Player player = hero.getPlayer();
        Location location = player.getLocation();
        if (location.getX() != x || location.getY() != y || location.getZ() != z) {
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            player.teleport(location);
        }
    }
}