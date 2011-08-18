package com.herocraftonline.dev.heroes.skill.skills;


import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.effects.BleedEffect;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillBandage extends TargettedSkill {

    public SkillBandage(Heroes plugin) {
        super(plugin, "Bandage");
        setDescription("Bandages the target");
        setUsage("/skill bandage <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[]{"skill bandage"});
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("health", 5);
        node.setProperty(SETTING_MAXDISTANCE, 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target instanceof Player) {
            Hero targetHero = getPlugin().getHeroManager().getHero((Player) target);
            int hpPlus = getSetting(hero.getHeroClass(), "health", 5);
            double targetHealth = targetHero.getHealth();

            if (targetHealth >= targetHero.getMaxHealth()) {
                Messaging.send(player, "Target is already fully healed.");
                return false;
            }

            PlayerInventory inv = player.getInventory();
            ItemStack inHand = inv.getItem(inv.getHeldItemSlot());

            if (!(inHand.getType() == Material.PAPER)) {
                Messaging.send(player, "You need paper to perform this.");
                return false;
            }
            
            HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(targetHero, hpPlus, this);
            getPlugin().getServer().getPluginManager().callEvent(hrhEvent);
            if (hrhEvent.isCancelled()) {
                Messaging.send(player, "Unable to heal the target at this time!");
                return false;
            }
            
            int amount = inHand.getAmount();
            if (amount > 1) {
                inHand.setAmount(amount - 1);
            } else {
                inv.setItemInHand(null);
            }
            
            
            targetHero.setHealth(targetHealth + hrhEvent.getAmount());
            targetHero.syncHealth();
            //update the map if this player is in the party
            if (targetHero.getParty() != null) {
                targetHero.getParty().setUpdateMapDisplay(true);
            }
            //Bandage cures Bleeding!
            for (Effect effect : targetHero.getEffects()) {
                if (effect instanceof BleedEffect) targetHero.removeEffect(effect);
            }

            broadcastExecuteText(hero, target);
            return true;
        }
        return false;
    }
}
