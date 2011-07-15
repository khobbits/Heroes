package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillBladegrasp extends ActiveSkill {
    
    private String applyText;
    private String expireText;

    public SkillBladegrasp(Heroes plugin) {
        super(plugin);
        setName("Bladegrasp");
        setDescription("Blocks incoming melee damage");
        setUsage("/skill bladegrasp");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill bladegrasp");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty("apply-text", "%hero% tightened his grip!");
        node.setProperty("expire-text", "%hero% parried an attack!");
        return node;
    }
    
    @Override
    public void init() {
        applyText = getSetting(null, "apply-text", "%hero% tightened his grip!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% parried an attack!").replace("%hero%", "$1");
    }
    
    @Override
    public boolean use(Hero hero, String[] args) {
        hero.addEffect(new BladegraspEffect(this));
        return true;
    }
    
    public class BladegraspEffect extends Effect {

        public BladegraspEffect(Skill skill) {
            super(skill, "Bladegrasp");
        }

        @Override
        public void apply(Hero hero) {
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
            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
                        event.setCancelled(true);
                        hero.removeEffect(hero.getEffect("Bladegrasp"));
                    }
                }
            }
        }

    }
}
