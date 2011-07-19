package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillReflect extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillReflect(Heroes plugin) {
        super(plugin);
        setName("Reflect");
        setDescription("Reflects all the damage done to you back to your target");
        setUsage("/skill reflect");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill reflect");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("duration", 5000);
        node.setProperty("reflected-amount", 0.5);
        node.setProperty("apply-text", "%hero% put up a reflective shield!");
        node.setProperty("expire-text", "%hero% lost his reflective shield!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%hero% put up a reflective shield!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% lost his reflective shield!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = getSetting(hero.getHeroClass(), "duration", 5000);
        hero.addEffect(new ReflectEffect(this, duration));

        return true;
    }

    public class ReflectEffect extends ExpirableEffect {

        public ReflectEffect(Skill skill, long duration) {
            super(skill, "Reflect", duration);
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
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent))
                return;

            EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) event;
            Entity defender = edbe.getEntity();
            Entity attacker = edbe.getDamager();
            if (attacker instanceof LivingEntity && defender instanceof Player) {
                Player defPlayer = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(defPlayer);
                if (hero.hasEffect("Reflect")) {
                    if (attacker instanceof Player) {
                        Player attPlayer = (Player) attacker;
                        if (plugin.getHeroManager().getHero(attPlayer).hasEffect(getName())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    LivingEntity attEntity = (LivingEntity) attacker;
                    int damage = (int) (event.getDamage() * getSetting(hero.getHeroClass(), "reflected-amount", 0.5));
                    plugin.getDamageManager().addSpellTarget((Entity) attacker);
                    attEntity.damage(damage, defender);
                }
            }
        }
    }
}
