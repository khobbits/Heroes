package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.HBlockListener;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;

public class SkillHerbalism extends PassiveSkill {

    
    private final Random rand = new Random();
    
    public SkillHerbalism(Heroes plugin) {
        super(plugin, "Herbalism");
        setDescription("You know about the things of the earth!");
        setIdentifiers(new String[] { "skill disarm" });
        setTypes(SkillType.KNOWLEDGE, SkillType.EARTH, SkillType.BUFF);

    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("chance-per-level", .01);
        return node;
    }

    public class SkillBlockListener extends BlockListener {

        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            if (event.isCancelled())
                return;
            
            Block block = event.getBlock();
            if (HBlockListener.placedBlocks.containsKey(block.getLocation()))
                return;
            
            int extraDrops = 0;
            switch (block.getType()) {
            case CROPS :
                extraDrops = 3;
                break;
            case SUGAR_CANE_BLOCK :
            case SAPLING :
            case LEAVES :
            case YELLOW_FLOWER :
            case RED_ROSE :
            case BROWN_MUSHROOM :
            case RED_MUSHROOM :
            case CACTUS :
            case JACK_O_LANTERN :
            case LONG_GRASS :
            case DEAD_BUSH :
                break;
            default:
                return;
            }
            
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect("Herbalism") || rand.nextDouble() > getSetting(hero.getHeroClass(), "chance-per-level", .02) * hero.getLevel())
                return;
            
            if (extraDrops != 0) {
                extraDrops = rand.nextInt(extraDrops) + 1;
            } else {
                extraDrops = 1;
            }
            
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), extraDrops, (short) 0, block.getData()));
        }
    }
}
