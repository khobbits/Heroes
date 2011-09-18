package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.CombustEffect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

public class SkillSoulFire extends ActiveSkill {
    
    private String applyText;
    private String expireText;
    private String igniteText;
    
    public SkillSoulFire(Heroes plugin) {
        super(plugin, "SoulFire");
        setDescription("Gives your attacks a chance to ignite the opponent");
        setUsage("/skill soulfire");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "skill soulfire" });
        
        setTypes(SkillType.FIRE, SkillType.BUFF, SkillType.SILENCABLE);
        
        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }
    
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("weapons", Util.swords);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero%'s weapon is sheathed in flame!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero%'s weapon is no longer aflame!");
        node.setProperty(Setting.DURATION.node(), 600000);
        node.setProperty("ignite-chance", 0.20);
        node.setProperty("ignite-duration", 5000);
        node.setProperty("ignite-text", "%hero% has lit %target% on fire with soulfire!");
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero%'s weapon is sheathed in flame!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero%'s weapon is no longer aflame!").replace("%hero%", "$1");
        igniteText = getSetting(null, "ignite-text", "%hero% has lit %target% on fire with soulfire!").replace("%hero%", "$1").replace("%target%", "$2");
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 600000);
        hero.addEffect(new SoulFireEffect(this, duration));
        return true;
    }

    public class SoulFireEffect extends ExpirableEffect {

        public SoulFireEffect(Skill skill, long duration) {
            super(skill, "SoulFire", duration);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DISPELLABLE);
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
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }
    }
    
    public class SkillDamageListener extends EntityListener {
        
        private final Skill skill;
        
        public SkillDamageListener (Skill skill) {
            this.skill = skill;
        }
        
        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent)) 
                return;
            
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (!(subEvent.getDamager() instanceof Player))
                return;
            
            Player player = (Player) subEvent.getDamager();
            Hero hero = plugin.getHeroManager().getHero(player);
            if (!getSetting(hero.getHeroClass(), "weapons", Util.swords).contains(player.getItemInHand().getType().name()) || !hero.hasEffect("SoulFire"))
                return;
            
            double chance = getSetting(hero.getHeroClass(), "ignite-chance", .2);
            if (Util.rand.nextDouble() >= chance)
                return;
            
            int fireTicks = getSetting(hero.getHeroClass(), "ignite-duration", 5000) / 50;
            Entity entity = event.getEntity();
            entity.setFireTicks(fireTicks);
            
            if (entity instanceof Player) {
                plugin.getHeroManager().getHero((Player) entity).addEffect(new CombustEffect(skill, (Player) player));
            } else if (entity instanceof Creature) {
                plugin.getHeroManager().addCreatureEffect((Creature) entity, new CombustEffect(skill, (Player) player));
            }
            
            String name = null;
            if (event.getEntity() instanceof Player) {
                name = ((Player) event.getEntity()).getName();
            } else if (event.getEntity() instanceof Creature) {
                name = Messaging.getCreatureName((Creature) event.getEntity());
            }
            
            broadcast(player.getLocation(), igniteText, player.getDisplayName(), name);
        }
    }
}
