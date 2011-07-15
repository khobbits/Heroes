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
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillManaShield extends ActiveEffectSkill {

    public SkillManaShield(Heroes plugin) {
        super(plugin);
        setName("ManaShield");
        setDescription("Uses your mana as a shield");
        setUsage("/skill manashield");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill manashield");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("mana-amount", 20);
        node.setProperty(SETTING_DURATION, 20000);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        String playerName = player.getName();
        applyEffect(hero);

        notifyNearbyPlayers(player.getLocation(), getUseText(), playerName, getName());
        return true;
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
                    event.setDamage(event.getDamage() / 2);
                    int mana = hero.getMana();
                    if (mana < absorbamount) {
                        hero.expireEffect(getName());
                    } else {
                        mana -= absorbamount;
                        hero.setMana(mana);
                        if (mana != 100 && hero.isVerbose()) {
                            Messaging.send(hero.getPlayer(), "Mana: " + Messaging.createManaBar(hero.getMana()));
                        }
                    }
                }
            }
        }
    }
}
