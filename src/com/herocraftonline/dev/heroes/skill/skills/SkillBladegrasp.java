package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.persistence.HeroEffects;
import com.herocraftonline.dev.heroes.skill.ActiveEffectSkill;

public class SkillBladegrasp extends ActiveEffectSkill {

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
    public boolean use(Hero hero, String[] args) {
        applyEffect(hero);
        notifyNearbyPlayers(hero.getPlayer().getLocation(), getUseText(), hero.getPlayer().getName(), getName());
        return true;
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                HeroEffects effects = plugin.getHeroManager().getHero(player).getEffects();
                if (effects.hasEffect(getName())) {
                    if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
                        event.setCancelled(true);
                        effects.expireEffect(getName());
                    }
                }
            }
        }

    }
}
