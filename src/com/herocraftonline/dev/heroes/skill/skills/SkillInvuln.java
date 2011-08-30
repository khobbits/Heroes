package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Beneficial;
import com.herocraftonline.dev.heroes.effects.Dispellable;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.Harmful;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillInvuln extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillInvuln(Heroes plugin) {
        super(plugin, "Invuln");
        setDescription("Grants total damage immunity");
        setUsage("/skill invuln");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill invuln" });

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% has become invulnerable!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% is once again vulnerable!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% has become invulnerable!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% is once again vulnerable!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
        // Remove any harmful effects on the caster
        for (Effect effect : hero.getEffects()) {
            if (effect instanceof Harmful) {
                hero.removeEffect(effect);
            }
        }
        hero.addEffect(new InvulnerabilityEffect(this, duration));
        return true;
    }

    public class InvulnerabilityEffect extends ExpirableEffect implements Dispellable, Beneficial {

        public InvulnerabilityEffect(Skill skill, long duration) {
            super(skill, "Invuln", duration);
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

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled()) {
                return;
            }

            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect("Invuln")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
