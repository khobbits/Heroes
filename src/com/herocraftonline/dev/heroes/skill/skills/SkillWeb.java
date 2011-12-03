package com.herocraftonline.dev.heroes.skill.skills;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillWeb extends TargettedSkill {

    private String applyText;
    private static Set<Location> changedBlocks = new HashSet<Location>();

    public SkillWeb(Heroes plugin) {
        super(plugin, "Web");
        setDescription("Catches your target in a web");
        setUsage("/skill web <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill web");
        setTypes(SkillType.EARTH, SkillType.SILENCABLE, SkillType.HARMFUL);

        registerEvent(Type.BLOCK_BREAK, new WebBlockListener(), Priority.Highest);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 5000); // in milliseconds
        node.set(Setting.APPLY_TEXT.node(), "%hero% conjured a web at %target%'s feet!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% conjured a web at %target%'s feet!").replace("%hero%", "$1").replace("%target%", "$2");
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        String name = "";
        if (target instanceof Player) {
            name = ((Player) target).getDisplayName();
        } else if (target instanceof Creature) {
            name = Messaging.getLivingEntityName((Creature) target).toLowerCase();
        }

        broadcast(player.getLocation(), applyText, player.getDisplayName(), name);
        int duration = getSetting(hero, Setting.DURATION.node(), 5000, false);
        WebEffect wEffect = new WebEffect(this, duration, target.getLocation().getBlock().getLocation());
        hero.addEffect(wEffect);
        return SkillResult.NORMAL;
    }

    public class WebBlockListener extends BlockListener {

        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            // Check out mappings to see if this block was a changed block, if so lets deny breaking it.
            if (changedBlocks.contains(event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }

    public class WebEffect extends ExpirableEffect {

        private List<Location> locations = new ArrayList<Location>();
        private Location loc;

        public WebEffect(Skill skill, long duration, Location location) {
            super(skill, "Web", duration);
            this.loc = location;
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            changeBlock(loc, hero);
            Block block = loc.getBlock();
            changeBlock(block.getRelative(BlockFace.DOWN).getLocation(), hero);
            for (BlockFace face : BlockFace.values()) {
                if (face.toString().contains("_") || face == BlockFace.UP || face == BlockFace.DOWN) {
                    continue;
                }
                Location blockLoc = block.getRelative(face).getLocation();
                changeBlock(blockLoc, hero);
                blockLoc = block.getRelative(getClockwise(face)).getLocation();
                changeBlock(blockLoc, hero);
                blockLoc = block.getRelative(face, 2).getLocation();
                changeBlock(blockLoc, hero);
            }
        }

        public Location getLocation() {
            return this.loc;
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            for (Location location : locations) {
                location.getBlock().setType(Material.AIR);
                changedBlocks.remove(location);
            }
            locations.clear();
        }

        private void changeBlock(Location location, Hero hero) {
            Block block = location.getBlock();

            if (block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.SNOW || block.getType() == Material.AIR) {
                changedBlocks.add(location);
                locations.add(location);
                location.getBlock().setType(Material.WEB);
            }
        }

        private BlockFace getClockwise(BlockFace face) {
            switch (face) {
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            case WEST:
            default:
                return BlockFace.SELF;
            }
        }
    }
}
