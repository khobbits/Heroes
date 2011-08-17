package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillGroupHeal extends ActiveSkill {

    public SkillGroupHeal(Heroes plugin) {
        super(plugin, "GroupHeal");
        setDescription("Heals all players around you");
        setUsage("/skill groupheal");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill groupheal"});
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("heal-amount", 2);
        node.setProperty("heal-radius", 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        int healAmount = getSetting(hero.getHeroClass(), "heal-amount", 2);
        if (hero.getParty() == null) {
            EntityRegainHealthEvent erhEvent = new EntityRegainHealthEvent(hero.getPlayer(), healAmount, RegainReason.CUSTOM);
            getPlugin().getServer().getPluginManager().callEvent(erhEvent);
            if (erhEvent.isCancelled()) {
                Messaging.send(hero.getPlayer(), "You can't heal right now!");
                return false;
            }
            healAmount = erhEvent.getAmount();
            //Heal just the caster if he's not in a party
            hero.setHealth(hero.getHealth() + healAmount);
            hero.syncHealth();
        } else {
            int radiusSquared = getSetting(hero.getHeroClass(), "heal-radius", 5)^2;
            Location heroLoc = hero.getPlayer().getLocation();
            //Heal party members near the caster
            for (Hero partyHero : hero.getParty().getMembers()) {
                if (!hero.getPlayer().getWorld().equals(partyHero.getPlayer().getWorld())) continue;
                if (partyHero.getPlayer().getLocation().distanceSquared(heroLoc) <= radiusSquared) {
                    EntityRegainHealthEvent erhEvent = new EntityRegainHealthEvent(partyHero.getPlayer(), healAmount, RegainReason.CUSTOM);
                    getPlugin().getServer().getPluginManager().callEvent(erhEvent);
                    if (erhEvent.isCancelled()) {
                        return false;
                    }
                    healAmount = erhEvent.getAmount();
                    partyHero.setHealth(partyHero.getHealth() + healAmount);
                    partyHero.syncHealth();
                }
            }
        }

        broadcastExecuteText(hero);
        return true;
    }
}
