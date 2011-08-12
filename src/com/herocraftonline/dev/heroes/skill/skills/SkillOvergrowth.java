package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

public class SkillOvergrowth extends ActiveSkill {

    private Random rand;

    public SkillOvergrowth(Heroes plugin) {
        super(plugin, "Overgrowth");
        setDescription("Turns a sapling into a full grown tree");
        setUsage("/skill overgrowth");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill overgrowth"});
        rand = new Random();
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("max-distance", 15);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int range = getSetting(hero.getHeroClass(), "max-distance", 15);
        if (player.getTargetBlock(null, range).getType() == Material.SAPLING) {
            Block targetBlock = player.getTargetBlock(null, range);
            TreeType tType = null;
            
            switch (targetBlock.getData()) {
            case (0x0) : 
                if (rand.nextInt(2) == 0) tType = TreeType.TREE;
                else tType = TreeType.BIG_TREE;
                break;
            case (0x1) : 
                if (rand.nextInt(2) == 0) tType = TreeType.REDWOOD;
                else tType = TreeType.TALL_REDWOOD;
                break;
            case (0x2) : 
                tType = TreeType.BIRCH;
                break;
            default :
                tType = TreeType.TREE;
            }

            targetBlock.setType(Material.AIR);
            player.getWorld().generateTree(targetBlock.getLocation(), tType);
            return true;
        } else {
            player.sendMessage(ChatColor.YELLOW + "Target is not a sapling!");
            return false;
        }
    }

}