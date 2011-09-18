package com.herocraftonline.dev.heroes.skill.skills;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.StunEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBlackjack extends ActiveSkill {

    private String applyText;
    private String expireText;

    private Random random = new Random();

    public SkillBlackjack(Heroes plugin) {
        super(plugin, "Blackjack");
        setDescription("Occasionally stuns your opponent");
        setUsage("/skill blackjack");
        setArgumentRange(0, 0);
        setIdentifiers("skill blackjack", "skill bjack");
        setTypes(SkillType.PHYSICAL, SkillType.BUFF);
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(this), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% prepared his blackjack!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% sheathed his blackjack!");
        node.setProperty("stun-duration", 5000);
        node.setProperty("stun-chance", 0.20);
        node.setProperty(Setting.DURATION.node(), 20000);
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% prepared his blackjack!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% sheathed his blackjack!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 20000);
        hero.addEffect(new BlackjackEffect(this, duration));
        return true;
    }

    public class BlackjackEffect extends ExpirableEffect {

        public BlackjackEffect(Skill skill, long duration) {
            super(skill, "Blackjack", duration);
            this.types.add(EffectType.BENEFICIAL);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
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

        private final Skill skill;

        public SkillEntityListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getCause() != DamageCause.ENTITY_ATTACK || !(subEvent.getDamager() instanceof Player)) {
                    return;
                }

                Hero attackingHero = plugin.getHeroManager().getHero((Player) subEvent.getDamager());
                if (!attackingHero.hasEffect("Blackjack")) {
                    return;
                }
                Hero defendingHero = plugin.getHeroManager().getHero((Player) event.getEntity());

                double chance = getSetting(attackingHero.getHeroClass(), "stun-chance", 0.20);
                if (random.nextDouble() < chance) {
                    int duration = getSetting(attackingHero.getHeroClass(), "stun-duration", 5000);
                    defendingHero.addEffect(new StunEffect(skill, duration));
                }
            }
        }

    }
}
