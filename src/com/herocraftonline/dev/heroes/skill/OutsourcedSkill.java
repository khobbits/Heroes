package com.herocraftonline.dev.heroes.skill;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Setting;

/**
 * Allows any plugin to be adapted into a Heroes skill via permissions restrictions. These permission based skills are
 * automatically created based on data in the permission skills section of the server's classes.yml file. Listed
 * permissions are automatically applied and removed when a player becomes eligible (correct class and level) for the
 * skill as defined in the config. There should not be any need to extend this class.
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
public class OutsourcedSkill extends Skill {

    private String[] permissions;

    /**
     * Constructor which defines the parameters required of any
     * {@link com.herocraftonline.dev.heroes.command.BaseCommand} as well as the permissions to be managed by this faux
     * skill. The description is automatically set to be the same as the usage so that the usage is readily displayed in
     * the skills list. No arguments are allowed for such a skill as it has no identifier to be executed with.
     * 
     * @param plugin
     *            the active Heroes instance
     * @param name
     *            the name of the skill
     * @param permissions
     *            the permissions to be managed by this skill
     * @param usage
     *            the usage text defined in the classes.yml config
     */
    public OutsourcedSkill(Heroes plugin, String name, String[] permissions, String usage) {
        super(plugin, name);
        setUsage(usage);
        setArgumentRange(0, 0);
        setDescription(usage);
        this.permissions = permissions;
        registerEvent(Type.CUSTOM_EVENT, new SkillHeroListener(), Priority.Monitor);
    }

    /**
     * Serves no purpose for an outsourced skill.
     */
    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        return true;
    }

    /**
     * Serves no purpose for an outsourced skill.
     */
    @Override
    public void init() {}

    /**
     * Grants this skill's associated permissions to the provided {@link Hero} if it is the correct class and level.
     * 
     * @param hero
     *            the <code>Hero</code> attempting to learn the skill
     */
    public void tryLearningSkill(Hero hero) {
        tryLearningSkill(hero, hero.getHeroClass());
    }

    /**
     * Grants this skill's associated permissions to the provided {@link Hero} if it is the level and the provided class
     * has the skill.
     * 
     * @param hero
     *            the <code>Hero</code> attempting to learn the skill
     * @param heroClass
     *            the {@link HeroClass} to check for this skill
     */
    public void tryLearningSkill(Hero hero, HeroClass heroClass) {
        Player player = hero.getPlayer();
        String world = player.getWorld().getName();
        String playerName = player.getName();
        ConfigurationNode settings = heroClass.getSkillSettings(getName());
        if (settings != null) {
            if (hero.getLevel() >= getSetting(heroClass, Setting.LEVEL.node(), 1)) {
                for (String permission : permissions) {
                    if (Heroes.Permissions != null && !hasPermission(world, playerName, permission)) {
                        addPermission(world, playerName, permission);
                    }
                    hero.addPermission(permission);
                }
            } else {
                for (String permission : permissions) {
                    if (Heroes.Permissions != null && hasPermission(world, playerName, permission)) {
                        removePermission(world, playerName, permission);
                    }
                    hero.removePermission(permission);
                }
            }
        } else {
            for (String permission : permissions) {
                if (Heroes.Permissions != null && hasPermission(world, playerName, permission)) {
                    removePermission(world, playerName, permission);
                }
                hero.removePermission(permission);
            }
        }
    }

    private void addPermission(String world, String player, String permission) {
        try {
            // Heroes.Permissions.safeGetUser(world, player).addPermission(permission); -- Incase we need it.
            Heroes.Permissions.safeGetUser(world, player).addTransientPermission(permission);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPermission(String world, String player, String permission) {
        try {
            return Heroes.Permissions.safeGetUser(world, player).hasPermission(permission);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removePermission(String world, String player, String permission) {
        try {
            Heroes.Permissions.safeGetUser(world, player).removePermission(permission);
            Heroes.Permissions.safeGetUser(world, player).removeTransientPermission(permission);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Monitors level and class change events and tries to give or remove the skill's permissions when appropriate.
     */
    public class SkillHeroListener extends HeroesEventListener {

        @Override
        public void onClassChange(ClassChangeEvent event) {
            if (event.isCancelled())
                return;
            
            tryLearningSkill(event.getHero(), event.getTo());
        }

        @Override
        public void onHeroChangeLevel(HeroChangeLevelEvent event) {
            tryLearningSkill(event.getHero());
        }
    }
}
