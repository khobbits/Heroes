package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillDamageEvent;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.common.FormEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillEndurance extends ActiveSkill {

    private String expireText;

    public SkillEndurance(Heroes plugin) {
        super(plugin, "Endurance");
        setDescription("You shift into a defensive form!");
        setUsage("/skill endurance");
        setArgumentRange(0, 0);
        setIdentifiers("skill endurance");
        setTypes(SkillType.BUFF, SkillType.PHYSICAL);

        registerEvent(Type.CUSTOM_EVENT, new SkillHeroListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("incoming-multiplier", .9);
        node.setProperty("outgoing-multiplier", .9);
        node.setProperty("multiplier-per-level", .005);
        node.setProperty(Setting.USE_TEXT.node(), "%hero% shifts into a defensive form!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% has shifted out of their defensive form!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% has shifted out of their defensive form!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        if (hero.hasEffect("Endurance")) {
            hero.removeEffect(hero.getEffect("Endurance"));
            return false;
        }
        hero.addEffect(new EnduranceEffect(this));
        broadcastExecuteText(hero);
        return true;
    }

    public class EnduranceEffect extends FormEffect {
        public EnduranceEffect(Skill skill) {
            super(skill, "Endurance");
            types.add(EffectType.PHYSICAL);
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            broadcast(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getDisplayName());
        }
    }

    public class SkillHeroListener extends HeroesEventListener {

        @Override
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled())
                return;

            if (event.getEntity() instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) event.getEntity());
                HeroClass heroClass = hero.getHeroClass();
                if (hero.hasEffect(getName())) {
                    double levelMult = getSetting(heroClass, "multiplier-per-level", .005) * hero.getLevel();
                    int newDamage = (int) (event.getDamage() * (getSetting(heroClass, "incoming-multiplier", .9) - levelMult));
                    event.setDamage(newDamage);
                }
            }

            if (event.getDamager() instanceof Player && event.getSkill().isType(SkillType.PHYSICAL)) {
                Hero hero = plugin.getHeroManager().getHero((Player) event.getDamager());
                if (hero.hasEffect(getName())) {
                    event.setDamage((int) (event.getDamage() * getSetting(hero.getHeroClass(), "outgoing-multiplier", .9)));
                }
            }
        }

        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled())
                return;

            if (event.getEntity() instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) event.getEntity());
                HeroClass heroClass = hero.getHeroClass();
                if (hero.hasEffect(getName())) {
                    double levelMult = getSetting(heroClass, "multiplier-per-level", .005) * hero.getLevel();
                    int newDamage = (int) (event.getDamage() * (getSetting(heroClass, "incoming-multiplier", .9) - levelMult));
                    event.setDamage(newDamage);

                }
            }

            if (event.getDamager() instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) event.getDamager());
                if (hero.hasEffect(getName())) {
                    event.setDamage((int) (event.getDamage() * getSetting(hero.getHeroClass(), "outgoing-multiplier", .9)));
                }
            }
        }
    }
}
