package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillBecomeDeath extends ActiveSkill {
    
    private String applyText;
    private String expireText;
    
    public SkillBecomeDeath(Heroes plugin) {
        super(plugin, "BecomeDeath");
        setDescription("You gain some of the features of an undead creature, and no longer need to breath air");
        setUsage("/skill becomedeath");
        setArgumentRange(0, 0);
        setIdentifiers("skill becomedeath", "bdeath");
        setTypes(SkillType.SILENCABLE, SkillType.BUFF, SkillType.DARK);
        
        SkillEntityListener eListener = new SkillEntityListener();
        registerEvent(Type.ENTITY_DAMAGE, eListener, Priority.Monitor);
        registerEvent(Type.ENTITY_TARGET, eListener, Priority.Highest);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 30000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% gains the features of a zombie!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% no longer appears as an undead!");
        return node;
    }
    
    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% gains the features of a zombie!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% no longer appears as an undead!").replace("%hero%", "$1");
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);
        int duration = getSetting(hero, Setting.DURATION.node(), 30000, false);
        hero.addEffect(new UndeadEffect(this, duration));
        return true;
    }
    
    public class UndeadEffect extends ExpirableEffect {

        public UndeadEffect(Skill skill, long duration) {
            super(skill, "Undead", duration);
            addMobEffect(13, (int) (duration / 1000) * 20, 0, false);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DARK);
            this.types.add(EffectType.WATER_BREATHING);
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
    
    public class SkillEntityListener extends EntityListener {


        @Override
        public void onEntityTarget(EntityTargetEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled() || !(event.getTarget() instanceof Player)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            if (!isUndead(event.getEntity())) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            Hero hero = plugin.getHeroManager().getHero((Player) event.getTarget());
            if (hero.hasEffect("Undead")) {
                event.setCancelled(true);
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Heroes.debug.startTask("HeroesSkillListener");
            if (event.isCancelled() || event.getDamage() == 0) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            if (!isUndead(event.getEntity()) || !(event instanceof EntityDamageByEntityEvent)) {
                Heroes.debug.stopTask("HeroesSkillListener");
                return;
            }
            
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (subEvent.getDamager() instanceof Player) {
                Hero hero = plugin.getHeroManager().getHero((Player) subEvent.getDamager());
                if (hero.hasEffect("Undead"))
                    hero.removeEffect(hero.getEffect("Undead"));
            } else if (subEvent.getDamager() instanceof Projectile) {
                if (((Projectile) subEvent.getDamager()).getShooter() instanceof Player) {
                    Hero hero = plugin.getHeroManager().getHero((Player) ((Projectile) subEvent.getDamager()).getShooter());
                    if (hero.hasEffect("Undead"))
                        hero.removeEffect(hero.getEffect("Undead"));
                }
            }
            Heroes.debug.stopTask("HeroesSkillListener");
        }
        
        private boolean isUndead(Entity entity) {
            return entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Ghast;
        }
    }
    
}
