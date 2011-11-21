package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainManaEvent;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillWisdom extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillWisdom(Heroes plugin) {
        super(plugin, "Wisdom");
        setDescription("You party benefits from increased mana regeneration!");
        setArgumentRange(0, 0);
        setUsage("/skill wisdom");
        setIdentifiers("skill wisdom");
        setTypes(SkillType.BUFF, SkillType.MANA, SkillType.SILENCABLE);

        registerEvent(Type.CUSTOM_EVENT, new SkillHeroListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("regen-multiplier", 1.2);
        node.setProperty(Setting.RADIUS.node(), 10);
        node.setProperty(Setting.DURATION.node(), 600000); // in Milliseconds - 10 minutes
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "Your feel a bit wiser!");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "You no longer feel as wise!");
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        int duration = getSetting(hero, Setting.DURATION.node(), 600000, false);
        double manaMultiplier = getSetting(hero, "regen-multiplier", 1.2, false);

        WisdomEffect mEffect = new WisdomEffect(this, duration, manaMultiplier);
        if (!hero.hasParty()) {
            if (hero.hasEffect("Wisdom")) {
                if (((WisdomEffect) hero.getEffect("Wisdom")).getManaMultiplier() > mEffect.getManaMultiplier()) {
                    Messaging.send(player, "You have a more powerful effect already!");
                }
            }
            hero.addEffect(mEffect);
        } else {
            int rangeSquared = (int) Math.pow(getSetting(hero, Setting.RADIUS.node(), 10, false), 2);
            for (Hero pHero : hero.getParty().getMembers()) {
                Player pPlayer = pHero.getPlayer();
                if (!pPlayer.getWorld().equals(player.getWorld())) {
                    continue;
                }
                if (pPlayer.getLocation().distanceSquared(player.getLocation()) > rangeSquared) {
                    continue;
                }
                if (pHero.hasEffect("Wisdom")) {
                    if (((WisdomEffect) pHero.getEffect("Wisdom")).getManaMultiplier() > mEffect.getManaMultiplier()) {
                        continue;
                    }
                }
                pHero.addEffect(mEffect);
            }
        }

        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }

    public class SkillHeroListener extends HeroesEventListener {

        @Override
        public void onHeroRegainMana(HeroRegainManaEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled()) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }

            if (event.getHero().hasEffect("Wisdom")) {
                event.setAmount((int) (event.getAmount() * getSetting(event.getHero(), "regen-multiplier", 1.2, false)));
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
    }

    public class WisdomEffect extends ExpirableEffect {

        private final double manaMultiplier;

        public WisdomEffect(Skill skill, long duration, double manaMultiplier) {
            super(skill, "Wisdom", duration);
            this.manaMultiplier = manaMultiplier;
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.BENEFICIAL);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            Messaging.send(player, applyText);
        }

        public double getManaMultiplier() {
            return manaMultiplier;
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            Messaging.send(player, expireText);
        }
    }
}
