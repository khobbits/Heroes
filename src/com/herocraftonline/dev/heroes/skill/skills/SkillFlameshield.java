package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillDamageEvent;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillFlameshield extends ActiveSkill {

    private String applyText;
    private String expireText;
    private String skillBlockText;

    public SkillFlameshield(Heroes plugin) {
        super(plugin, "Flameshield");
        setDescription("Fire can't hurt you!");
        setUsage("/skill flameshield");
        setArgumentRange(0, 0);
        setIdentifiers("skill flameshield", "skill fshield");

        setTypes(SkillType.FIRE, SkillType.SILENCABLE, SkillType.BUFF);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
        registerEvent(Type.CUSTOM_EVENT, new HeroesSkillListener(), Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 5000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% conjured a shield of flames!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% lost his shield of flames!");
        node.setProperty("skill-block-text", "%name%'s flameshield has blocked %hero%'s %skill%.");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% conjured a shield of flames!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% lost his shield of flames!").replace("%hero%", "$1");
        skillBlockText = getSetting(null, "skill-block-text", "%name%'s flameshield has blocked %hero%'s %skill%.").replace("%name%", "$1").replace("%hero%", "$2").replace("%skill%", "$3");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 5000);
        hero.addEffect(new FlameshieldEffect(this, duration));

        return true;
    }

    public class FlameshieldEffect extends ExpirableEffect {

        public FlameshieldEffect(Skill skill, long duration) {
            super(skill, "Flameshield", duration);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.FIRE);
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

    public class HeroesSkillListener extends HeroesEventListener {

        @Override
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled())
                return;
            if (!event.getSkill().isType(SkillType.FIRE))
                return;

            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect("Flameshield")) {
                    String name = event.getDamager().getPlayer().getName();
                    String skillName = event.getSkill().getName().toLowerCase();
                    broadcast(event.getEntity().getLocation(), skillBlockText, player.getName(), name, skillName);
                    event.setCancelled(true);
                }
            } else if (event.getEntity() instanceof Creature) {
                Creature creature = (Creature) event.getEntity();
                if (plugin.getEffectManager().creatureHasEffect(creature, "Flameshield")) {
                    String name = event.getDamager().getPlayer().getName();
                    String skillName = event.getSkill().getName().toLowerCase();
                    broadcast(event.getEntity().getLocation(), skillBlockText, Messaging.getCreatureName(creature), name, skillName);
                    event.setCancelled(true);
                }
            }
        }
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled())
                return;

            if (event.getCause() != DamageCause.FIRE && event.getCause() != DamageCause.FIRE_TICK && event.getCause() != DamageCause.LAVA)
                return;

            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
