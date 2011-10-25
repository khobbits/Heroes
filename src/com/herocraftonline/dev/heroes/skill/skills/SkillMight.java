package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillMight extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillMight(Heroes plugin) {
        super(plugin, "Might");
        setDescription("You increase your party's damage with weapons!");
        setArgumentRange(0, 0);
        setUsage("/skill might");
        setIdentifiers("skill might");
        setTypes(SkillType.BUFF, SkillType.SILENCABLE);

        registerEvent(Type.CUSTOM_EVENT, new SkillHeroListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("damage-bonus", 1.25);
        node.setProperty(Setting.RADIUS.node(), 10);
        node.setProperty(Setting.APPLY_TEXT.node(), "Your muscles bulge with power!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "You feel strength leave your body!");
        node.setProperty(Setting.DURATION.node(), 600000); // in Milliseconds - 10 minutes
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "Your muscles bulge with power!");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "You feel strength leave your body!");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int duration = getSetting(hero, Setting.DURATION.node(), 600000, false);
        double damageBonus = getSetting(hero, "damage-bonus", 1.25, false);

        MightEffect mEffect = new MightEffect(this, duration, damageBonus);
        if (!hero.hasParty()) {
            if (hero.hasEffect("Might")) {
                if (((MightEffect) hero.getEffect("Might")).getDamageBonus() > mEffect.getDamageBonus()) {
                    Messaging.send(player, "You have a more powerful effect already!");
                }
            }
            hero.addEffect(mEffect);
        } else {
            int range = getSetting(hero, Setting.RADIUS.node(), 10, false);
            int rangeSquared = range * range;
            Location loc = player.getLocation();
            for (Hero pHero : hero.getParty().getMembers()) {
                Player pPlayer = pHero.getPlayer();
                if (!pPlayer.getWorld().equals(player.getWorld())) {
                    continue;
                }
                if (pPlayer.getLocation().distanceSquared(loc) > rangeSquared) {
                    continue;
                }
                if (pHero.hasEffect("Might")) {
                    if (((MightEffect) pHero.getEffect("Might")).getDamageBonus() > mEffect.getDamageBonus()) {
                        continue;
                    }
                }
                pHero.addEffect(mEffect);
            }
        }

        broadcastExecuteText(hero);
        return true;
    }

    public class MightEffect extends ExpirableEffect {

        private final double damageBonus;

        public MightEffect(Skill skill, long duration, double damageBonus) {
            super(skill, "Might", duration);
            this.damageBonus = damageBonus;
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.BENEFICIAL);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            Messaging.send(player, applyText);
        }

        public double getDamageBonus() {
            return damageBonus;
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            Messaging.send(player, expireText);
        }
    }

    public class SkillHeroListener extends HeroesEventListener {

        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.getCause() != DamageCause.ENTITY_ATTACK) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                Hero hero = plugin.getHeroManager().getHero(player);

                if (hero.hasEffect("Might")) {
                    double damageBonus = ((MightEffect) hero.getEffect("Might")).getDamageBonus();
                    event.setDamage((int) (event.getDamage() * damageBonus));
                }
            } else if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    Player player = (Player) ((Projectile) event.getDamager()).getShooter();
                    Hero hero = plugin.getHeroManager().getHero(player);

                    if (hero.hasEffect("Might")) {
                        double damageBonus = ((MightEffect) hero.getEffect("Might")).getDamageBonus();
                        event.setDamage((int) (event.getDamage() * damageBonus));
                    }
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }
}
