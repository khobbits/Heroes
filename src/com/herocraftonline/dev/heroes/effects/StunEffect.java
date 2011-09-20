package com.herocraftonline.dev.heroes.effects;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class StunEffect extends PeriodicExpirableEffect {

    private static final long period = 100;
    private final String stunApplyText = "$1 is stunned!";
    private final String stunExpireText = "$1 is no longer stunned!";
    private MobEffect mobEffect = new MobEffect(9, 0, 0);

    private Location loc;

    public StunEffect(Skill skill, long duration) {
        super(skill, "Stun", period, duration);
        this.types.add(EffectType.STUN);
        this.types.add(EffectType.HARMFUL);
        this.types.add(EffectType.PHYSICAL);
        this.types.add(EffectType.DISABLE);
        this.mobEffect = new MobEffect(9, (int) (duration / 1000) * 20, 127);
    }

    @Override
    public void apply(Hero hero) {
        super.apply(hero);

        Player player = hero.getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.netServerHandler.sendPacket(new Packet41MobEffect(entityPlayer.id, this.mobEffect));
        loc = player.getLocation();
        broadcast(loc, stunApplyText, player.getDisplayName());
    }

    @Override
    public void remove(Hero hero) {
        super.remove(hero);

        Player player = hero.getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.netServerHandler.sendPacket(new Packet42RemoveMobEffect(entityPlayer.id, this.mobEffect));
        broadcast(player.getLocation(), stunExpireText, player.getDisplayName());
    }

    @Override
    public void tick(Hero hero) {
        super.tick(hero);

        Location location = hero.getPlayer().getLocation();
        if (location.getX() != loc.getX() || location.getY() != loc.getY() || location.getZ() != loc.getZ()) {
            loc.setYaw(location.getYaw());
            loc.setPitch(location.getPitch());
            hero.getPlayer().teleport(loc);
        }
    }
}
