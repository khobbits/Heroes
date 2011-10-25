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
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillAbsorb extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillAbsorb(Heroes plugin) {
        super(plugin, "Absorb");
        setDescription("Converts half the damage you take into mana");
        setUsage("/skill absorb");
        setArgumentRange(0, 0);
        setIdentifiers("skill absorb");
        setTypes(SkillType.SILENCABLE, SkillType.BUFF, SkillType.MANA);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("mana-amount", 20);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is absorbing damage!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "Absorb faded from %target%!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is absorbing damage!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "Absorb faded from %target%!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        hero.addEffect(new AbsorbEffect(this));
        return true;
    }

    public class AbsorbEffect extends Effect {

        public AbsorbEffect(Skill skill) {
            super(skill, "Absorb");
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DISPELLABLE);
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

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect("Absorb")) {
                    int absorbAmount = getSetting(hero, "mana-amount", 20, false);
                    event.setDamage((int) (event.getDamage() * 0.50));
                    int mana = hero.getMana();
                    if (mana + absorbAmount > 100) {
                        hero.removeEffect(hero.getEffect("Absorb"));
                    } else {
                        hero.setMana(mana + absorbAmount);
                        if (hero.isVerbose()) {
                            Messaging.send(player, ChatColor.BLUE + "MANA " + Messaging.createManaBar(mana + absorbAmount));
                        }
                    }
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
