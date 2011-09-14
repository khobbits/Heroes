package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSafefallOther extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillSafefallOther(Heroes plugin) {
        super(plugin, "SafefallOther");
        setDescription("Stops your target from taking fall damage for a short amount of time");
        setUsage("/skill safefallother <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill safefallother");

        setTypes(SkillType.MOVEMENT, SkillType.BUFF, SkillType.SILENCABLE);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 10000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% has gained safefall!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has lost safefall!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% has gained safefall!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has lost safefall!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (target instanceof Player) {
            Hero targetHero = plugin.getHeroManager().getHero((Player) target);

            broadcastExecuteText(hero, target);

            int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 10000);
            targetHero.addEffect(new SafefallEffect(this, duration));

            return true;
        } else {
            return false;
        }
    }

    public class SafefallEffect extends ExpirableEffect {

        public SafefallEffect(Skill skill, long duration) {
            super(skill, "Safefall", duration);
            this.types.add(EffectType.DISPELLABLE);
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

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || event.getCause() != DamageCause.FALL) {
                return;
            }

            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect("Safefall")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
