package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillSafefallOther extends TargettedSkill {

    private String applyText;
    private String expireText;

    public SkillSafefallOther(Heroes plugin) {
        super(plugin);
        setName("SafefallOther");
        setDescription("Stops your target from taking fall damage for a short amount of time");
        setUsage("/skill safefallother <target>");
        setMinArgs(0);
        setMaxArgs(1);
        getIdentifiers().add("skill safefallother");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty("duration", 10000);
        node.setProperty("apply-text", "%target% has gained safefall!");
        node.setProperty("expire-text", "%target% has lost safefall!");
        return node;
    }

    @Override
    public void init() {
        applyText = getSetting(null, "apply-text", "%target% has gained safefall!").replace("%target%", "$1");
        expireText = getSetting(null, "expire-text", "%target% has lost safefall!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        if (target instanceof Player) {
            Hero targetHero = plugin.getHeroManager().getHero((Player) target);

            int duration = getSetting(hero.getHeroClass(), "duration", 10000);
            targetHero.addEffect(new SafefallEffect(this, duration));

            return true;
        } else {
            return false;
        }
    }

    public class SafefallEffect extends ExpirableEffect {

        public SafefallEffect(Skill skill, long duration) {
            super(skill, "Safefall", duration);
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
