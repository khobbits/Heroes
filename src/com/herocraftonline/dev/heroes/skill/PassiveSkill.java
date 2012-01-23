package com.herocraftonline.dev.heroes.skill;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

/**
 * A skill that provides a passive bonus to a {@link Hero}. The skill's effects are automatically applied when a Hero of
 * the appropriate class reaches the level specified in classes.yml. Because this skill is passive, there is no need to
 * override the {@link #execute(CommandSender, String[]) execute} nor
 * {@link com.herocraftonline.dev.heroes.command.BaseCommand#setUsage(String) use}. Messages displayed when the passive
 * effect is applied or removed are automatically pulled from the configs. By default, the effect applied is simply the
 * name of the skill. This can be changed by overriding {@link #apply(Hero) apply} and {@link #unapply(Hero) unapply}.
 * </br>
 * </br>
 * <b>Skill Framework:</b>
 * <ul>
 * <li>{@link ActiveSkill}</li>
 * <ul>
 * <li>{@link ActiveEffectSkill}</li>
 * <li>{@link TargettedSkill}</li>
 * </ul>
 * <li>{@link PassiveSkill}</li> <li>{@link OutsourcedSkill}</li> </ul>
 */
public abstract class PassiveSkill extends Skill {

    private String applyText = null;
    private String unapplyText = null;
    private EffectType[] effectTypes = null;

    /**
     * Typical skill constructor, except that it automatically sets the usage text to <i>Passive Skill</i>, which should
     * not be changed for normal use. There should be no identifiers defined as a passive skill is not meant to be
     * executed.
     * 
     * @param plugin
     *            the active Heroes instance
     */
    public PassiveSkill(Heroes plugin, String name) {
        super(plugin, name);
        setUsage("Passive Skill");
        Bukkit.getServer().getPluginManager().registerEvents(new SkillListener(), plugin);
    }

    /**
     * Serves no purpose for a passive skill.
     */
    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        Messaging.send(sender, "$1 is a passive skill and cannot be used!", getName());
        return true;
    }

    /**
     * Creates and returns a <code>ConfigurationNode</code> containing the default apply and unapply texts. When using
     * additional configuration settings in your skills, be sure to override this method to define them with defaults.
     * 
     * @return a default configuration
     */
    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set(Setting.APPLY_TEXT.node(), "%hero% gained %skill%!");
        section.set(Setting.UNAPPLY_TEXT.node(), "%hero% lost %skill%!");
        return section;
    }

    /**
     * Loads and stores the skill's apply and unapply texts from the configuration. By default, these texts are
     * "%hero% gained %skill%!" and "%hero% lost %skill%!", where %hero% and %skill% are replaced with the
     * Hero's and skill's names, respectively.
     */
    @Override
    public void init() {
        applyText = SkillConfigManager.getRaw(this, Setting.APPLY_TEXT, "");
        applyText = applyText.replace("%hero%", "$1").replace("%skill%", "$2");
        unapplyText = SkillConfigManager.getRaw(this, Setting.UNAPPLY_TEXT, "");
        unapplyText = unapplyText.replace("%hero%", "$1").replace("%skill%", "$2");
    }

    public void setEffectTypes(EffectType...effectTypes) {
        this.effectTypes = effectTypes;
    }

    /**
     * Attempts to apply this skill's effect to the provided {@link Hero} if the it is the correct class and level.
     * 
     * @param hero
     *            the Hero to try applying the effect to
     */
    public void tryApplying(Hero hero) {
        if (!hero.hasAccessToSkill(this)) {
            return;
        }

        if (hero.canUseSkill(this) && !Heroes.properties.disabledWorlds.contains(hero.getPlayer().getWorld().getName())) {
            if (!hero.hasEffect(getName())) {
                apply(hero);
            }
        } else {
            unapply(hero);
        }
    }

    /**
     * Applies the effect to the provided {@link Hero}.
     * 
     * @param hero
     *            the Hero to apply the effect to
     */
    protected void apply(Hero hero) {
        Effect effect = new Effect(this, getName(), effectTypes);
        effect.setPersistent(true);
        hero.addEffect(effect);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), applyText, player.getDisplayName(), getName());
    }

    /**
     * Removes the effect from the provided {@link Hero}.
     * 
     * @param hero
     *            the Hero to remove the effect from
     */
    protected void unapply(Hero hero) {
        if (hero.hasEffect(getName())) {
            hero.removeEffect(hero.getEffect(getName()));
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), unapplyText, player.getDisplayName(), getName());
        }
    }

    /**
     * Monitors level and class change events and tries to apply or remove the skill's effect when appropriate.
     */
    public class SkillListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onClassChange(ClassChangeEvent event) {
            tryApplying(event.getHero());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onHeroChangeLevel(HeroChangeLevelEvent event) {
            tryApplying(event.getHero());
        }
    }
}
