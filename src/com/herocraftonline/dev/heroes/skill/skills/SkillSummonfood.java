package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;

public class SkillSummonfood extends ActiveSkill {

    public SkillSummonfood(Heroes plugin) {
        super(plugin, "Summonfood");
        setDescription("Summons you food!");
        setUsage("/skill summonfood");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill summonfood" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("food-type", "BREAD");
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        World world = player.getWorld();
        HeroClass heroClass = hero.getHeroClass();
        ItemStack dropItem = new ItemStack(Material.matchMaterial(getSetting(heroClass, "food-type", "BREAD")), 1);
        world.dropItem(player.getLocation(), dropItem);
        broadcastExecuteText(hero);
        return true;
    }

}
