package com.herocraftonline.dev.heroes.skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;

public class SkillManager {
    
    private Map<String, Skill> skills;
    private Map<String, Skill> identifiers;
    private Set<String> skillFiles;
    private final Heroes plugin;
    private final File dir;
    
    public SkillManager(Heroes plugin) {
        skills = new LinkedHashMap<String, Skill>();
        identifiers = new HashMap<String, Skill>();
        skillFiles = new HashSet<String>();
        this.plugin = plugin;
        dir = new File(plugin.getDataFolder(), "skills");
        dir.mkdir();
        for (String skillName : dir.list()) {
            if (skillName.contains(".jar"))
                skillFiles.add(skillName.replace(".jar", "").toLowerCase());
        }
    }

    /**
     * Adds a skill to the skill mapping
     * 
     * @param command
     */
    public void addSkill(Skill command) {
        skills.put(command.getName().toLowerCase(), command);
        for (String ident : command.getIdentifiers()) {
            identifiers.put(ident.toLowerCase(), command);
        }
    }

    /**
     * Removes a skill from the skill mapping
     * 
     * @param command
     */
    public void removeSkill(Skill command) {
        skills.remove(command);
        for (String ident : command.getIdentifiers()) {
            identifiers.remove(ident.toLowerCase());
        }
    }

    /**
     * Returns a skill from it's name
     * If the skill is not in the skill mapping it will attempt to load it from file
     * 
     * @param name
     * @return
     */
    public Skill getSkill(String name) {
        if (!isLoaded(name)) {
            loadSkill(name);
        }
        return skills.get(name.toLowerCase());
    }
    
    /**
     * 
     * Returns a collection of all skills loaded in the skill manager
     * @return
     */
    public Collection<Skill> getSkills() {
        return Collections.unmodifiableCollection(skills.values());
    }
    
    /**
     * Gets a skill from it's identifiers
     * 
     * @param ident
     * @param executor
     * @return
     */
    public Skill getSkillFromIdent(String ident, CommandSender executor) {
        if ( identifiers.get(ident.toLowerCase()) == null) {
            for (Skill skill : skills.values()) {
                if (skill.isIdentifier(executor, ident))
                    return skill;
            }
        }
        return identifiers.get(ident.toLowerCase());
    }

    /**
     * Returns a loaded skill from a skill jar
     * 
     * @param file
     * @return
     */
    public Skill loadSkill(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();

            String mainClass = null;
            while (entries.hasMoreElements()) {
                JarEntry element = entries.nextElement();
                if (element.getName().equalsIgnoreCase("skill.info")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
                    mainClass = reader.readLine().substring(12);
                    break;
                }
            }

            if (mainClass != null) {
                ClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() }, plugin.getClass().getClassLoader());
                Class<?> clazz = Class.forName(mainClass, true, loader);
                for (Class<?> subclazz : clazz.getClasses()) {
                    Class.forName(subclazz.getName(), true, loader);
                }
                Class<? extends Skill> skillClass = clazz.asSubclass(Skill.class);
                Constructor<? extends Skill> ctor = skillClass.getConstructor(plugin.getClass());
                Skill skill = ctor.newInstance(plugin);
                plugin.getConfigManager().loadSkillConfig(skill);
                return skill;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            Heroes.log(Level.INFO, "The skill " + file.getName() + " failed to load");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * loads a Skill from file
     * 
     * @param name
     * @return
     */
    public boolean loadSkill(String name) {
        if (isLoaded(name))
            return true;
        
        name = "skill" + name.toLowerCase();
        if (skillFiles.contains(name)) {
            Skill skill = loadSkill(new File(dir, name + ".jar"));
            if (skill == null)
                return false;
            
            addSkill(skill);
        }
        return true;
    }
    
    /**
     * Checks if a skill has already been loaded
     * 
     * @param name
     * @return
     */
    public boolean isLoaded(String name) {
        return skills.containsKey(name.toLowerCase());
    }
    
    /**
     * Load all the skills.
     */
    public void loadSkills() {       
        ArrayList<String> loadedSkills = new ArrayList<String>();
        for (String f : dir.list()) {
            if (f.contains(".jar")) {
                //if the Skill is already loaded, skip it
                if (isLoaded(f.replace(".jar", "")))
                    continue;
                
                Skill skill = loadSkill(new File(dir, f));
                if (skill != null) {
                    addSkill(skill);
                    if (loadedSkills.isEmpty()) {
                        Heroes.log(Level.INFO, "Collecting and loading skills");
                    }
                    loadedSkills.add(skill.getName());
                    plugin.debugLog(Level.INFO, "Skill " + skill.getName() + " Loaded");
                }
            }
        }
    }
}
