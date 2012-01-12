package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;

import net.minecraft.server.EntityHuman;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillTelekinesis extends ActiveSkill {

    public SkillTelekinesis(Heroes plugin) {
        super(plugin, "Telekinesis");
        setDescription("You can activate levers, buttons and other interactable objects from afar.");
        setUsage("/skill telekinesis");
        setArgumentRange(0, 0);
        setIdentifiers("skill telekinesis");
        setTypes(SkillType.FORCE, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.MAX_DISTANCE.node(), 15);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        HashSet<Byte> transparent = new HashSet<Byte>();
        transparent.add((byte) Material.AIR.getId());
        transparent.add((byte) Material.WATER.getId());
        transparent.add((byte) Material.REDSTONE_TORCH_ON.getId());
        transparent.add((byte) Material.REDSTONE_TORCH_OFF.getId());
        transparent.add((byte) Material.REDSTONE_WIRE.getId());
        transparent.add((byte) Material.TORCH.getId());
        transparent.add((byte) Material.SNOW.getId());
        Block block = player.getTargetBlock(transparent, SkillConfigManager.getUseSetting(hero, this, Setting.MAX_DISTANCE, 15, false));
        if (block.getType() == Material.LEVER || block.getType() == Material.STONE_BUTTON) {
            EntityHuman eH = ((CraftPlayer) player).getHandle();
            // Can't adjust levers/Buttons through CB
            net.minecraft.server.Block.byId[block.getTypeId()].interact(((CraftWorld) block.getWorld()).getHandle(), block.getX(), block.getY(), block.getZ(), eH);
            // In Case Bukkit eaver fixes blockState changes on levers:
            // Lever lever = (Lever) block.getState().getData();
            // lever.setPowered(!lever.isPowered());
            // block.getState().update();
            broadcastExecuteText(hero);
            return SkillResult.NORMAL;
        }

        Messaging.send(player, "You must target a lever or button!");
        return SkillResult.INVALID_TARGET_NO_MSG;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }

}
