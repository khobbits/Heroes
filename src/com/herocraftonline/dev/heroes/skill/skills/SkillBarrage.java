package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillBarrage extends ActiveSkill {

    public SkillBarrage(Heroes plugin) {
        super(plugin, "Barrage");
        setDescription("Fire a Barrage of Arrows around you.");
        setUsage("/skill barrage");
        setArgumentRange(0, 0);
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.HARMFUL);
        setIdentifiers("skill barrage");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        PlayerInventory inv = player.getInventory();

        Map<Integer, ? extends ItemStack> arrowSlots = inv.all(Material.ARROW);

        int numArrows = 0;
        for (Map.Entry<Integer, ? extends ItemStack> entry : arrowSlots.entrySet()) {
            numArrows += entry.getValue().getAmount();
        }

        if (numArrows == 0) {
            Messaging.send(player, "You have no arrows.");
            return false;
        }

        numArrows = numArrows > 24 ? 24 : numArrows;

        int removedArrows = 0;
        for (Map.Entry<Integer, ? extends ItemStack> entry : arrowSlots.entrySet()) {
            int amount = entry.getValue().getAmount();
            int remove = amount;
            if (removedArrows + remove > numArrows) {
                remove = numArrows - removedArrows;
            }
            removedArrows += remove;
            if (remove == amount) {
                inv.clear(entry.getKey());
            } else {
                inv.getItem(entry.getKey()).setAmount(amount - remove);
            }

            if (removedArrows >= numArrows) {
                break;
            }
        }
        player.updateInventory();

        double diff = 2 * Math.PI / numArrows;
        for (double a = 0; a < 2 * Math.PI; a += diff) {
            Vector vel = new Vector(Math.cos(a), 0, Math.sin(a));
            player.shootArrow().setVelocity(vel);
        }
        broadcastExecuteText(hero);
        return true;
    }
}
