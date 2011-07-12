package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveEffectSkill;

public class SkillAbsorb extends ActiveEffectSkill {

    public SkillAbsorb(Heroes plugin) {
        super(plugin);
        setName("Absorb");
        setDescription("Converts all damage into mana");
        setUsage("/skill absorb");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill absorb");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        String playerName = player.getName();
        applyEffect(hero);

        notifyNearbyPlayers(player.getLocation(), getUseText(), playerName, getName());
        return true;
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("mana-amount", 20);
        return node;
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled()) {
                return;
            }

            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    int absorbamount = getSetting(hero.getHeroClass(), "mana-amount", 20);
                    event.setDamage((int) (event.getDamage() * 0.50));
                    if (hero.getMana() + absorbamount > 100) {
                        hero.expireEffect(getName());
                    } else {
                        hero.setMana(hero.getMana() + absorbamount);
                    }
                }
            }
        }
    }
}
