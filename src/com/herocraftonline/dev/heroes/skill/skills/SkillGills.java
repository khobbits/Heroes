package com.herocraftonline.dev.heroes.skill.skills;

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
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillGills extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillGills(Heroes plugin) {
        super(plugin, "Gills");
        setDescription("Negate drowning damage");
        setUsage("/skill gills");
        setArgumentRange(0, 0);
        setIdentifiers("skill gills");
        setTypes(SkillType.SILENCABLE, SkillType.BUFF);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 30000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% has grown a set of gills!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% lost his gills!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% has grown a set of gills!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% lost his gills!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 5000);
        hero.addEffect(new GillsEffect(this, duration));

        return true;
    }

    public class GillsEffect extends ExpirableEffect {

        public GillsEffect(Skill skill, long duration) {
            super(skill, "Gills", duration);
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
            if (event.isCancelled() || !(event.getCause() == DamageCause.DROWNING)) {
                return;
            }

            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect("Gills")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
