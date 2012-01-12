package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSuperheat extends ActiveSkill {

    private BlockListener playerListener = new SkillPlayerListener();
    private String applyText;
    private String expireText;

    public SkillSuperheat(Heroes plugin) {
        super(plugin, "Superheat");
        setDescription("Your pickaxe smelts ores as you mine them for $1 seconds.");
        setUsage("/skill superheat");
        setArgumentRange(0, 0);
        setIdentifiers("skill superheat");
        setTypes(SkillType.FIRE, SkillType.EARTH, SkillType.BUFF, SkillType.SILENCABLE);

        registerEvent(Type.BLOCK_BREAK, playerListener, Priority.Highest);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set(Setting.DURATION.node(), 20000);
        section.set(Setting.APPLY_TEXT.node(), "%hero%'s pick has become superheated!");
        section.set(Setting.EXPIRE_TEXT.node(), "%hero%'s pick has cooled down!");
        return section;
    }

    @Override
    public void init() {
        super.init();
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "%hero%'s pick has become superheated!").replace("%hero%", "$1");
        expireText = SkillConfigManager.getRaw(this, Setting.EXPIRE_TEXT, "%hero%'s pick has cooled down!").replace("%hero%", "$1");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 20000, false);
        hero.addEffect(new SuperheatEffect(this, duration));

        return SkillResult.NORMAL;
    }

    public class SkillPlayerListener extends BlockListener {

        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (hero.hasEffect("Superheat")) {
                Block block = event.getBlock();
                switch (block.getType()) {
                    case IRON_ORE:
                        event.setCancelled(true);
                        block.setType(Material.AIR);
                        block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.IRON_INGOT, 1));
                        break;
                    case GOLD_ORE:
                        event.setCancelled(true);
                        block.setType(Material.AIR);
                        block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
                        break;
                    case SAND:
                        event.setCancelled(true);
                        block.setType(Material.AIR);
                        block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.GLASS, 1));
                        break;
                    case COBBLESTONE:
                        event.setCancelled(true);
                        block.setType(Material.AIR);
                        block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.STONE, 1));
                        break;
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }

    public class SuperheatEffect extends ExpirableEffect {

        public SuperheatEffect(Skill skill, long duration) {
            super(skill, "Superheat", duration);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.FIRE);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

    }

    @Override
    public String getDescription(Hero hero) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 20000, false);
        return getDescription().replace("$1", duration / 1000 + "");
    }

}
