package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.HBlockListener;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillMining extends PassiveSkill {

    public SkillMining(Heroes plugin) {
        super(plugin, "Mining");
        setDescription("You have a $1% chance to get extra ores when mining!");
        setEffectTypes(EffectType.BENEFICIAL);
        setTypes(SkillType.KNOWLEDGE, SkillType.EARTH, SkillType.BUFF);
        Bukkit.getServer().getPluginManager().registerEvents(new SkillBlockListener(this), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.CHANCE_LEVEL.node(), .001);
        node.set("chance-from-stone", .0005);
        return node;
    }

    public class SkillBlockListener implements Listener {

        private Skill skill;
        
        SkillBlockListener(Skill skill) {
            this.skill = skill;
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onBlockBreak(BlockBreakEvent event) {
            if (event.isCancelled()) {
                return;
            }
            
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect("Mining")) {
                return;
            }
            Block block = event.getBlock();
            if (HBlockListener.placedBlocks.containsKey(block.getLocation())) {
                return;
            }

            Material dropMaterial = null;
            boolean isStone = false;
            switch (block.getType()) {
                case IRON_ORE:
                case GOLD_ORE:
                    dropMaterial = block.getType();
                    break;
                case DIAMOND_ORE:
                    dropMaterial = Material.DIAMOND;
                    break;
                case COAL_ORE:
                    dropMaterial = Material.COAL;
                    break;
                case REDSTONE_ORE:
                    dropMaterial = Material.REDSTONE;
                    break;
                case LAPIS_BLOCK:
                    dropMaterial = Material.INK_SACK;
                    break;
                case STONE:
                    isStone = true;
                    break;
                default:
                    return;
            }

            double chance = Util.rand.nextDouble();
            
            if (isStone && chance <= SkillConfigManager.getUseSetting(hero, skill, "chance-from-stone", .0005, false) * hero.getSkillLevel(skill)) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(getMatFromHeight(block), 1));
                return;
            } else if (isStone) {
                return;
            }
            
            if (chance >= SkillConfigManager.getUseSetting(hero, skill, Setting.CHANCE_LEVEL, .001, false) * hero.getSkillLevel(skill)) {
                return;
            }
            if (dropMaterial == Material.INK_SACK) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(dropMaterial, 1, (short) 0, (byte) 4));
            } else {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(dropMaterial, 1));
            }
        }

        private Material getMatFromHeight(Block block) {
            int y = block.getY();

            if (y < 20)
                return Material.DIAMOND;
            else if (y < 40)
                return Material.GOLD_ORE;
            else if (y < 60)
                return Material.IRON_ORE;
            else
                return Material.COAL_ORE;
        }
    }

    @Override
    public String getDescription(Hero hero) {
        double chance = SkillConfigManager.getUseSetting(hero, this, Setting.CHANCE_LEVEL, .001, false);
        int level = hero.getSkillLevel(this);
        if (level < 1)
            level = 1;
        return getDescription().replace("$1", Util.stringDouble(chance * level * 100));
    }
}
