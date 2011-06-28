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

public class SkillSummonArrow extends ActiveSkill{

    public SkillSummonArrow(Heroes plugin) {
        super(plugin);
        name = "SummonArrow";
        description = "Summons you some arrows!";
        usage = "/skill summonarrow";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("skill summonarrow");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("amount", 1);
        return node;
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        World world = player.getWorld();
        HeroClass heroClass = hero.getHeroClass();
        ItemStack dropItem = new ItemStack(Material.ARROW, getSetting(heroClass, "amount", 1));
        world.dropItem(player.getLocation(), dropItem);
        return true;
    }

}
