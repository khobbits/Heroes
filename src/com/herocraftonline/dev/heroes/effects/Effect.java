package com.herocraftonline.dev.heroes.effects;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class Effect {

    protected final String name;
    protected final Skill skill;
    protected final Heroes plugin;
    protected final Set<EffectType> types = EnumSet.noneOf(EffectType.class);
    protected long applyTime;
    private boolean persistent;
    private MobEffect mobEffect = null;

    public Effect(Skill skill, String name) {
        this.name = name;
        this.skill = skill;
        if (skill != null) {
            this.plugin = skill.plugin;
        } else {
            this.plugin = null;
        }
        this.persistent = false;
    }

    public Effect(Skill skill, String name, EffectType... types) {
        this(skill, name);
        for (EffectType type : types) {
            this.types.add(type);
        }
    }

    public void apply(Creature creature) {
        this.applyTime = System.currentTimeMillis();
    }

    public void apply(Hero hero) {
        this.applyTime = System.currentTimeMillis();
        if (mobEffect != null) {
            EntityPlayer ePlayer = ((CraftPlayer) hero.getPlayer()).getHandle();
            ePlayer.netServerHandler.sendPacket(new Packet41MobEffect(ePlayer.id, this.mobEffect));
        }
    }

    public void broadcast(Location source, String message, Object... args) {
        skill.broadcast(source, message, args);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Effect other = (Effect) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /**
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return the Skill
     */
    public Skill getSkill() {
        return skill;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    /**
     * Returns if the effect is persistent or not
     * 
     * @return
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Returns if this Effect is the type specified
     * 
     * @param type
     * @return
     */
    public boolean isType(EffectType type) {
        return types.contains(type);
    }

    public void remove(Creature creature) {}

    public void remove(Hero hero) {
        if (mobEffect != null) {
            EntityPlayer ePlayer = ((CraftPlayer) hero.getPlayer()).getHandle();
            ePlayer.netServerHandler.sendPacket(new Packet42RemoveMobEffect(ePlayer.id, this.mobEffect));
        }
    }

    /*
     * Sets the effects persistence value
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public void setMobEffect(int id, int strength, int ticks) {
        this.mobEffect = new MobEffect(id, ticks, strength);
    }

    public MobEffect getMobEffect() {
        return mobEffect;
    }
}
