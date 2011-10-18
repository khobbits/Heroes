package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillJump extends ActiveSkill {

    private static final Set<Material> noJumpMaterials;
    static {
        noJumpMaterials = new HashSet<Material>();
        noJumpMaterials.add(Material.WATER);
        noJumpMaterials.add(Material.AIR);
        noJumpMaterials.add(Material.LAVA);
        noJumpMaterials.add(Material.LEAVES);
        noJumpMaterials.add(Material.SOUL_SAND);
    }
    
    public SkillJump(Heroes plugin) {
        super(plugin, "Jump");
        setDescription("Launches you into the air");
        setUsage("/skill jump");
        setArgumentRange(0, 0);
        setIdentifiers("skill jump");
        setTypes(SkillType.MOVEMENT, SkillType.PHYSICAL);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("no-air-jump", true);
        return node;
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        Material mat = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        if ((getSetting(hero.getHeroClass(), "no-air-jump", true) && noJumpMaterials.contains(mat)) || player.isInsideVehicle()) {
            Messaging.send(player, "You can't jump while mid-air or from inside a vehicle!");
            return false;
        }
        float pitch = player.getEyeLocation().getPitch();
        int jumpForwards = 1;
        if (pitch > 45) {
            jumpForwards = 1;
        }
        if (pitch > 0) {
            pitch = -pitch;
        }
        float multiplier = (90f + pitch) / 50f;
        Vector v = player.getVelocity().setY(1).add(player.getLocation().getDirection().setY(0).normalize().multiply(multiplier * jumpForwards));
        player.setVelocity(v);
        player.setFallDistance(-8f);
        broadcastExecuteText(hero);
        
        return true;
    }
}
