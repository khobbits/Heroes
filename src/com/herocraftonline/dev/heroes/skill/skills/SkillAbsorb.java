package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillAbsorb extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillAbsorb(Heroes plugin) {
        super(plugin, "Absorb");
        setDescription("Converts all damage into mana");
        setUsage("/skill absorb");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill absorb"});

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("mana-amount", 20);
        node.setProperty("apply-text", "%target% is absorbing damage");
        node.setProperty("expire-text", "Absorb faded from %target%!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%target% is absorbing damage!").replace("%target%", "$1");
        expireText = getSetting(null, "expire-text", "Absorb faded from %target%!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        hero.addEffect(new AbsorbEffect(this));
        return true;
    }

    public class AbsorbEffect extends Effect implements Dispellable {

        public AbsorbEffect(Skill skill) {
            super(skill, "Absorb");
        }

        @Override
        public void apply(Hero hero) {
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

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
                Hero hero = getPlugin().getHeroManager().getHero(player);
                if (hero.hasEffect("Absorb")) {
                    int absorbAmount = getSetting(hero.getHeroClass(), "mana-amount", 20);
                    event.setDamage((int) (event.getDamage() * 0.50));
                    if (hero.getMana() + absorbAmount > 100) {
                        hero.removeEffect(hero.getEffect("Absorb"));
                    } else {
                        hero.setMana(hero.getMana() + absorbAmount);
                        if (hero.isVerbose()) {
                            Messaging.send(hero.getPlayer(), ChatColor.BLUE + "MANA " + Messaging.createManaBar(hero.getMana()));
                        }
                    }
                }
            }
        }
    }
}
