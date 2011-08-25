package com.herocraftonline.dev.heroes.skill;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.effects.Effect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Setting;

/**
 * A skill that provides a passive bonus to a {@link Hero}. The skill's effects are automatically applied when a Hero of
 * the appropriate class reaches the level specified in classes.yml. Because this skill is passive, there is no need to
 * override the {@link #execute(CommandSender, String[]) execute} nor {@link com.herocraftonline.dev.heroes.command.BaseCommand#setUsage(String) use}. Messages displayed when the passive
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

    /**
     * Typical skill constructor, except that it automatically sets the usage text to <i>Passive Skill</i>, which should
     * not be changed for normal use. There should be no identifiers defined as a passive skill is not meant to be
     * executed.
     * 
     * @param plugin the active Heroes instance
     */
    public PassiveSkill(Heroes plugin, String name) {
        super(plugin, name);
        setUsage("Passive Skill");

        registerEvent(Type.CUSTOM_EVENT, new SkillCustomEventListener(), Priority.Monitor);
    }

    /**
     * Serves no purpose for a passive skill.
     */
    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        return true;
    }

    /**
     * Creates and returns a <code>ConfigurationNode</code> containing the default apply and unapply texts. When using
     * additional configuration settings in your skills, be sure to override this method to define them with defaults.
     * 
     * @return a default configuration
     */
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% gained %skill%!");
        node.setProperty(Setting.UNAPPLY_TEXT.node(), "%hero% lost %skill%!");
        return node;
    }

    /**
     * Loads and stores the skill's apply and unapply texts from the configuration. By default, these texts are
     * "%hero% gained %skill%!" and "%hero% lost %skill%!", where %hero% and %skill% are replaced with the
     * Hero's and skill's names, respectively.
     */
    @Override
    public void init() {
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% gained %skill%!");
        applyText = applyText.replace("%hero%", "$1").replace("%skill%", "$2");
        unapplyText = getSetting(null, Setting.UNAPPLY_TEXT.node(), "%hero% lost %skill%!");
        unapplyText = unapplyText.replace("%hero%", "$1").replace("%skill%", "$2");
    }

    /**
     * Attempts to apply this skill's effect to the provided {@link Hero} if the it is the correct class and level.
     * 
     * @param hero the Hero to try applying the effect to
     */
    public void tryApplying(Hero hero) {
        HeroClass heroClass = hero.getHeroClass();
        if (!heroClass.hasSkill(getName()))
            return;
        ConfigurationNode settings = heroClass.getSkillSettings(getName());
        if (settings != null) {
            if (hero.getLevel() >= getSetting(heroClass, Setting.LEVEL.node(), 1)) {
                apply(hero);
            } else {
                unapply(hero);
            }
        }
    }

    /**
     * Applies the effect to the provided {@link Hero}.
     * 
     * @param hero the Hero to apply the effect to
     */
    protected void apply(Hero hero) {
        Effect effect = new Effect(this, getName());
        effect.setPersistent(true);
        hero.addEffect(effect);
        Player player = hero.getPlayer();
        broadcast(player.getLocation(), applyText, player.getDisplayName(), getName());
    }

    /**
     * Removes the effect from the provided {@link Hero}.
     * 
     * @param hero the Hero to remove the effect from
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
    public class SkillCustomEventListener extends CustomEventListener {

        @Override
        public void onCustomEvent(Event event) {
            if (event instanceof HeroChangeLevelEvent) {
                HeroChangeLevelEvent subEvent = (HeroChangeLevelEvent) event;
                if (!subEvent.isCancelled()) {
                    tryApplying(subEvent.getHero());
                }
            } else if (event instanceof ClassChangeEvent) {
                ClassChangeEvent subEvent = (ClassChangeEvent) event;
                if (subEvent.isCancelled()) {
                    tryApplying(subEvent.getHero());
                }
            }
        }
    }

}
