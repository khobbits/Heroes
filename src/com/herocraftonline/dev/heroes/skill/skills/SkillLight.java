package com.herocraftonline.dev.heroes.skill.skills;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.PeriodicExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillLight extends ActiveSkill {

    private String applyText;
    private String expireText;
    
    public static Set<Material> allowedBlocks = EnumSet.noneOf(Material.class);
    static {
        allowedBlocks.add(Material.DIRT);
        allowedBlocks.add(Material.GRASS);
        allowedBlocks.add(Material.STONE);
        allowedBlocks.add(Material.COBBLESTONE);
        allowedBlocks.add(Material.WOOD);
        allowedBlocks.add(Material.LOG);
        allowedBlocks.add(Material.NETHERRACK);
        allowedBlocks.add(Material.SOUL_SAND);
        allowedBlocks.add(Material.SANDSTONE);
        allowedBlocks.add(Material.GLASS);
        allowedBlocks.add(Material.WOOL);
        allowedBlocks.add(Material.DOUBLE_STEP);
        allowedBlocks.add(Material.BRICK);
        allowedBlocks.add(Material.OBSIDIAN);
        allowedBlocks.add(Material.NETHER_BRICK);
        allowedBlocks.add(Material.MOSSY_COBBLESTONE);
    }

    public SkillLight(Heroes plugin) {
        super(plugin, "Light");
        setArgumentRange(0, 0);
        setTypes(SkillType.BUFF, SkillType.LIGHT, SkillType.SILENCABLE);
        setUsage("/skill light");
        setIdentifiers("skill light");
    }
    
    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 30000); // in milliseconds
        node.set(Setting.PERIOD.node(), 200); // in milliseconds
        node.set(Setting.APPLY_TEXT.node(), "%hero% is lighting the way.");
        node.set(Setting.EXPIRE_TEXT.node(), "%hero% is no longer lighting the way");
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "%hero% is lighting the way.").replace("%hero%", "$1");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%hero% is no longer lighting the way.").replace("%hero%", "$1");
    }
    
    @Override
    public SkillResult use(Hero hero, String[] args) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 30000, false);
        int period = SkillConfigManager.getUseSetting(hero, this, Setting.PERIOD, 200, false);
        hero.addEffect(new LightEffect(this, period, duration));
        return SkillResult.NORMAL;
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }

    public class LightEffect extends PeriodicExpirableEffect {

        private Location lastLoc = null;
        private Byte lastData = null;
        private Material lastMat = null;

        public LightEffect(Skill skill, long period, long duration) {
            super(skill, "Light", period, duration);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.LIGHT);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), applyText, p.getDisplayName());
            Block thisBlock = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (allowedBlocks.contains(thisBlock.getType())) {
                lastLoc = thisBlock.getLocation();
                lastMat = thisBlock.getType();
                lastData = thisBlock.getData();
                p.sendBlockChange(lastLoc, Material.GLOWSTONE, (byte) 0);
            }
        }

        @Override
        public void tick(Hero hero) {
            super.tick(hero);
            Player p = hero.getPlayer();
            Block thisBlock = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (thisBlock.getLocation().equals(lastLoc)) {
                return;
            } else if (allowedBlocks.contains(thisBlock.getType())) {
                if (lastLoc != null) {
                    p.sendBlockChange(lastLoc, lastMat, lastData);
                }
                lastLoc = thisBlock.getLocation();
                lastMat = thisBlock.getType();
                lastData = thisBlock.getData();
                p.sendBlockChange(lastLoc, Material.GLOWSTONE, (byte) 0);
            } else if (lastLoc != null) {
                p.sendBlockChange(lastLoc, lastMat, lastData);
            }
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), expireText, p.getDisplayName());
            if (lastLoc != null) {
                p.sendBlockChange(lastLoc, lastMat, lastData);
            }
        }
    }
}
