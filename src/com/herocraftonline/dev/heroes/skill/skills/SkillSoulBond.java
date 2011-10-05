package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillDamageEvent;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSoulBond extends TargettedSkill {

    private String expireText;

    public SkillSoulBond(Heroes plugin) {
        super(plugin, "SoulBond");
        setDescription("You share your targets pain.");
        setUsage("/skill soulbond <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill soulbond", "skill sbond");
        setTypes(SkillType.SILENCABLE, SkillType.LIGHT, SkillType.BUFF);

        registerEvent(Type.CUSTOM_EVENT, new SkillHeroesListener(), Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 300000);
        node.setProperty("damage-multiplier", .5);
        node.setProperty(Setting.RADIUS.node(), 25);
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target%'s soul is no longer bound to %hero%!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target%'s soul is no longer bound to %hero%!").replace("%target%", "$1").replace("%hero%", "$2");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target.equals(player) || target instanceof Creature && !hero.getSummons().contains(target)) {
            Messaging.send(player, "Invalid Target!");
            return false;
        }

        if (target instanceof Player && (!hero.hasParty() || !hero.getParty().isPartyMember(plugin.getHeroManager().getHero((Player) target)))) {
            Messaging.send(player, "Invalid Target!");
            return false;
        }

        // Remove the previous effect before applying a new one
        if (hero.hasEffect("SoulBond")) {
            hero.removeEffect(hero.getEffect("SoulBond"));
        }

        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 300000);
        SoulBondedEffect sbEffect = new SoulBondedEffect(this, player);
        hero.addEffect(new SoulBondEffect(this, duration, target, sbEffect));

        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(sbEffect);
        } else {
            plugin.getEffectManager().addCreatureEffect((Creature) target, sbEffect);
        }

        broadcastExecuteText(hero, target);
        return true;
    }

    public class SkillHeroesListener extends HeroesEventListener {

        @Override
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled())
                return;

            if (event.getEntity() instanceof Player) {
                Hero tHero = plugin.getHeroManager().getHero((Player) event.getEntity());

                // Make sure the target doesn't have both effects
                if (tHero.hasEffect("SoulBonded") && !tHero.hasEffect("SoulBond")) {
                    Player applier = ((SoulBondedEffect) tHero.getEffect("SoulBonded")).getApplier();
                    Hero hero = plugin.getHeroManager().getHero(applier);
                    HeroClass heroClass = hero.getHeroClass();

                    // Distance check
                    int radius = getSetting(heroClass, Setting.RADIUS.node(), 25);
                    int radiusSquared = radius * radius;
                    if (applier.getLocation().distanceSquared(event.getEntity().getLocation()) > radiusSquared)
                        return;

                    // Split the damage
                    int splitDamage = (int) (event.getDamage() * getSetting(heroClass, "damage-multiplier", .5));
                    applier.damage(splitDamage, event.getDamager().getPlayer());
                    event.setDamage(event.getDamage() - splitDamage);
                }
            } else if (event.getEntity() instanceof Creature) {
                if (!plugin.getEffectManager().creatureHasEffect((Creature) event.getEntity(), "SoulBonded"))
                    return;

                Player applier = ((SoulBondedEffect) plugin.getEffectManager().getCreatureEffect((Creature) event.getEntity(), "SoulBonded")).getApplier();
                Hero hero = plugin.getHeroManager().getHero(applier);
                HeroClass heroClass = hero.getHeroClass();

                // Distance check
                int radius = getSetting(heroClass, Setting.RADIUS.node(), 25);
                int radiusSquared = radius * radius;
                if (applier.getLocation().distanceSquared(event.getEntity().getLocation()) > radiusSquared)
                    return;

                // Split the damage
                int splitDamage = (int) (event.getDamage() * getSetting(heroClass, "damage-multiplier", .5));
                applier.damage(splitDamage, event.getDamager().getPlayer());
                event.setDamage(event.getDamage() - splitDamage);
            }
        }

        @Override
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled())
                return;

            if (event.getEntity() instanceof Player) {
                Hero tHero = plugin.getHeroManager().getHero((Player) event.getEntity());

                // Make sure the target doesn't have both effects
                if (tHero.hasEffect("SoulBonded") && !tHero.hasEffect("SoulBond")) {
                    Player applier = ((SoulBondedEffect) tHero.getEffect("SoulBonded")).getApplier();
                    Hero hero = plugin.getHeroManager().getHero(applier);
                    HeroClass heroClass = hero.getHeroClass();

                    // Distance check
                    int radius = getSetting(heroClass, Setting.RADIUS.node(), 25);
                    int radiusSquared = radius * radius;
                    if (applier.getLocation().distanceSquared(event.getEntity().getLocation()) > radiusSquared)
                        return;

                    // Split the damage
                    int splitDamage = (int) (event.getDamage() * getSetting(heroClass, "damage-multiplier", .5));
                    applier.damage(splitDamage, event.getDamager());
                    event.setDamage(event.getDamage() - splitDamage);
                }
            } else if (event.getEntity() instanceof Creature) {
                if (!plugin.getEffectManager().creatureHasEffect((Creature) event.getEntity(), "SoulBonded"))
                    return;

                Player applier = ((SoulBondedEffect) plugin.getEffectManager().getCreatureEffect((Creature) event.getEntity(), "SoulBonded")).getApplier();
                Hero hero = plugin.getHeroManager().getHero(applier);
                HeroClass heroClass = hero.getHeroClass();

                // Distance check
                int radius = getSetting(heroClass, Setting.RADIUS.node(), 25);
                int radiusSquared = radius * radius;
                if (applier.getLocation().distanceSquared(event.getEntity().getLocation()) > radiusSquared)
                    return;

                // Split the damage
                int splitDamage = (int) (event.getDamage() * getSetting(heroClass, "damage-multiplier", .5));
                applier.damage(splitDamage, event.getDamager());
                event.setDamage(event.getDamage() - splitDamage);
            }
        }

    }

    public class SoulBondedEffect extends Effect {

        private final Player applier;

        public SoulBondedEffect(Skill skill, Player applier) {
            super(skill, "SoulBonded");
            this.applier = applier;
        }

        public Player getApplier() {
            return applier;
        }
    }

    public class SoulBondEffect extends ExpirableEffect {

        private final LivingEntity target;
        private final Effect bondEffect;

        public SoulBondEffect(Skill skill, long duration, LivingEntity target, Effect bondEffect) {
            super(skill, "SoulBond", duration);
            this.target = target;
            this.bondEffect = bondEffect;
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DISPELLABLE);
        }

        public LivingEntity getTarget() {
            return target;
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            String name = null;

            if (target instanceof Player) {
                name = ((Player) target).getDisplayName();
                plugin.getHeroManager().getHero((Player) target).removeEffect(bondEffect);
            } else {
                name = Messaging.getCreatureName((Creature) target);
                plugin.getEffectManager().removeCreatureEffect((Creature) target, bondEffect);
            }

            broadcast(player.getLocation(), expireText, name, player.getDisplayName());
        }
    }
}
