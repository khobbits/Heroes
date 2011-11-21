package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillGroupHeal extends ActiveSkill {

    public SkillGroupHeal(Heroes plugin) {
        super(plugin, "GroupHeal");
        setDescription("Heals all players around you");
        setUsage("/skill groupheal");
        setArgumentRange(0, 0);
        setIdentifiers("skill groupheal", "skill gheal");
        setTypes(SkillType.HEAL, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("heal-amount", 2);
        node.setProperty(Setting.RADIUS.node(), 5);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int healAmount = getSetting(hero, "heal-amount", 2, false);
        if (hero.getParty() == null) {
            // Heal just the caster if he's not in a party
            HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, healAmount, this);
            plugin.getServer().getPluginManager().callEvent(hrhEvent);
            if (hrhEvent.isCancelled()) {
                Messaging.send(player, "Unable to heal the target at this time!");
                return SkillResult.FAIL;
            }
            hero.setHealth(hero.getHealth() + hrhEvent.getAmount());
            hero.syncHealth();
        } else {
            int radiusSquared = (int) Math.pow(getSetting(hero, Setting.RADIUS.node(), 5, false), 2);
            Location heroLoc = player.getLocation();
            // Heal party members near the caster
            for (Hero partyHero : hero.getParty().getMembers()) {
                if (!player.getWorld().equals(partyHero.getPlayer().getWorld())) {
                    continue;
                }
                if (partyHero.getPlayer().getLocation().distanceSquared(heroLoc) <= radiusSquared) {
                    HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(partyHero, healAmount, this);
                    plugin.getServer().getPluginManager().callEvent(hrhEvent);
                    if (hrhEvent.isCancelled()) {
                        Messaging.send(player, "Unable to heal the target at this time!");
                        return SkillResult.FAIL;
                    }
                    partyHero.setHealth(partyHero.getHealth() + hrhEvent.getAmount());
                    partyHero.syncHealth();
                }
            }
        }

        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }
}
