package com.herocraftonline.dev.heroes.api;

/**
 * Defines a SkillResult for a Skill to return. Includes Arguments for the default messages, and a boolean to display the message, or ignore it
 * If you are unsure about return types for Skills, use the Default static return types for your skill.
 * 
 *
 */
public class SkillResult {
    
    public final Object[] args;
    public final ResultType type;
    public final boolean showMessage;
    
    // Default SkillResult returns that don't require a message, these can be used without needing to continually remake the object
    public static final SkillResult FAIL = new SkillResult(ResultType.FAIL, false);
    public static final SkillResult INVALID_TARGET = new SkillResult(ResultType.INVALID_TARGET, true);
    public static final SkillResult LOW_MANA = new SkillResult(ResultType.LOW_MANA, true);
    public static final SkillResult LOW_HEALTH = new SkillResult(ResultType.LOW_HEALTH, true);
    public static final SkillResult NORMAL = new SkillResult(ResultType.NORMAL, false);
    public static final SkillResult SKIP_POST_USAGE = new SkillResult(ResultType.SKIP_POST_USAGE, false);
    public static final SkillResult START_DELAY = new SkillResult(ResultType.START_DELAY, false);
    public static final SkillResult CANCELLED = new SkillResult(ResultType.CANCELLED, false);
    public static final SkillResult REMOVED_EFFECT = new SkillResult(ResultType.REMOVED_EFFECT, false);
    public static final SkillResult INVALID_TARGET_NO_MSG = new SkillResult(ResultType.INVALID_TARGET, false);
    
    /**
     * Constructs a new SkillResult from the provided arguments for use with a SkillCompleteEvent
     * args passed are used for default messages which can be found in {@link ActiveSkill}
     * 
     * @param type
     * @param showMessage
     * @param args
     */
    public SkillResult(ResultType type, boolean showMessage, Object...args) {
        this.type = type;
        this.args = args;
        this.showMessage = showMessage;
    }
    
    /**
     * Defines the ResultType of the SkillReturn
     */
    public enum ResultType {
        CANCELLED,
        INVALID_TARGET,
        FAIL,
        LOW_MANA,
        LOW_HEALTH,
        LOW_LEVEL,
        MISSING_REAGENT,
        NORMAL,
        ON_GLOBAL_COOLDOWN,
        ON_COOLDOWN,
        REMOVED_EFFECT,
        SKIP_POST_USAGE,
        START_DELAY;
    }
}
