package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.HBlockListener;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillHerbalism extends PassiveSkill {

    public SkillHerbalism(Heroes plugin) {
        super(plugin, "Herbalism");
        setDescription("You know about the things of the earth!");
        setEffectTypes(EffectType.BENEFICIAL);
        setTypes(SkillType.KNOWLEDGE, SkillType.EARTH, SkillType.BUFF);
        
        registerEvent(Type.BLOCK_BREAK, new SkillBlockListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.CHANCE_LEVEL.node(), .001);
        return node;
    }

    public class SkillBlockListener extends BlockListener {

        private Skill skill;
        
        SkillBlockListener(Skill skill) {
            this.skill = skill;
        }
        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            Block block = event.getBlock();
            if (HBlockListener.placedBlocks.containsKey(block.getLocation())) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            int extraDrops = 0;
            Material mat = null;
            switch (block.getType()) {
                case CROPS:
                    extraDrops = 3;
                    mat = Material.WHEAT;
                    break;
                case SUGAR_CANE_BLOCK:
                    mat = Material.SUGAR_CANE;
                    extraDrops = 2;
                    break;
                case MELON_BLOCK:
                    mat = Material.MELON;
                    extraDrops = 7;
                    break;
                case SAPLING:
                case LEAVES:
                case YELLOW_FLOWER:
                case RED_ROSE:
                case BROWN_MUSHROOM:
                case RED_MUSHROOM:
                case CACTUS:
                case LONG_GRASS:
                case PUMPKIN:
                case DEAD_BUSH:
                    break;
                default:
                    Heroes.debug.stopTask("HeroesSkillListener");
                    return;
            }

            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect("Herbalism") || Util.rand.nextDouble() >= getSetting(hero, "chance-per-level", .001, false) * hero.getLevel(skill)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            if (extraDrops != 0) {
                extraDrops = Util.rand.nextInt(extraDrops) + 1;
            } else {
                extraDrops = 1;
            }
            if (mat != null)
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(mat, extraDrops));
            else
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), extraDrops, (short) 0, block.getData()));
            
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
