package com.herocraftonline.dev.heroes.skill;

import org.bukkit.command.CommandSender;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

/**
 * Similar to {@link ActiveSkill} except for the inclusion of helper methods for applying an effect on a Hero that will
 * automatically last for a duration specified in the config. When the effect expires, an expiry message is
 * automatically displayed to nearby users. To apply an effect, simply call {@link #applyEffect(Hero) applyEffect} from
 * your {@link ActiveSkill#execute(CommandSender, String[]) execute} method. The {@link #onExpire(Hero) onExpire} method
 * can be overriden if additional behavior is desired when an effect expires. The effect applied defaults to the skill's
 * name.
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
public abstract class ActiveEffectSkill extends ActiveSkill {

    /**
     * Identifier used to store expiry text setting
     */
    public final String SETTING_EXPIRETEXT = "expire-text";

    /**
     * Identifier used to store effect duration setting
     */
    public final String SETTING_DURATION = "effect-duration";

    private String expireText;

    /**
     * When defining your own constructor, be sure to assign the name, description, usage, argument bounds and
     * identifier fields as defined in {@link com.herocraftonline.dev.heroes.command.BaseCommand}. Remember that each
     * identifier must begin with <i>skill</i>.
     * 
     * @param plugin
     *            the active Heroes instance
     */
    public ActiveEffectSkill(Heroes plugin) {
        super(plugin);
    }

    /**
     * Creates and returns a <code>ConfigurationNode</code> containing the default usage text, expiry text and effect
     * duration. When using additional configuration settings in your skills, be sure to override this method to define
     * them with defaults.
     * 
     * @return a default configuration
     */
    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty(SETTING_USETEXT, "%hero% gained %skill%!");
        node.setProperty(SETTING_EXPIRETEXT, "%hero% lost %skill%!");
        node.setProperty(SETTING_DURATION, 10000);
        return node;
    }

    /**
     * Returns the effect expiry message
     * 
     * @return the effect expire text
     */
    public String getExpireText() {
        return expireText;
    }

    /**
     * Loads and stores the skill's effect gain and loss texts from the configuration. By default, these texts are
     * "%hero% gained %skill%!" and "%hero% lost %skill%!" where %hero% and %skill% are replaced with the Hero's and
     * skill's names, respectively.
     */
    @Override
    public void init() {
        String useText = getSetting(null, SETTING_USETEXT, "%hero% gained %skill%!");
        useText = useText.replace("%hero%", "$1").replace("%skill%", "$2");
        setUseText(useText);

        String expireText = getSetting(null, SETTING_EXPIRETEXT, "%hero% lost %skill%!");
        expireText = expireText.replace("%hero%", "$1").replace("%skill%", "$2");
        setExpireText(expireText);
    }

    /**
     * Displays the expiry text to players near the effected Hero
     * 
     * @param hero
     *            the Hero to use as a source location
     */
    public void onExpire(Hero hero) {
        notifyNearbyPlayers(hero.getPlayer().getLocation(), expireText, hero.getPlayer().getName(), getName());
    }

    /**
     * Manually modifies the effect expiry message
     * 
     * @param expireText
     *            the effect expire text
     */
    public void setExpireText(String expireText) {
        this.expireText = expireText;
    }

    /**
     * Applies an effect with the skill's name for the configured duration to the specified Hero
     * 
     * @param hero
     *            the Hero to which the effect is applied
     */
    protected void applyEffect(Hero hero) {
        hero.applyEffect(getName(), getSetting(hero.getHeroClass(), SETTING_DURATION, 10000));
    }
}
