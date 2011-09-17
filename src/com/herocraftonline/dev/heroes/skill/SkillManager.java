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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;

public class SkillManager {
    
    private Map<String, Skill> skills;
    private Map<String, Skill> identifiers;
    private final Heroes plugin;
    
    public SkillManager(Heroes plugin) {
        skills = new LinkedHashMap<String, Skill>();
        identifiers = new HashMap<String, Skill>();
        this.plugin = plugin;
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
     * 
     * @param name
     * @return
     */
    public Skill getSkill(String name) {
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
                return ctor.newInstance(plugin);
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
     * Load all the skills.
     */
    public void loadSkills() {
        File dir = new File(plugin.getDataFolder(), "skills");
        ArrayList<String> skNo = new ArrayList<String>();
        dir.mkdir();
        boolean added = false;
        for (String f : dir.list()) {
            if (f.contains(".jar")) {
                Skill skill = loadSkill(new File(dir, f));
                if (skill != null) {
                    addSkill(skill);
                    if (!added) {
                        Heroes.log(Level.INFO, "Collecting and loading skills");
                        added = true;
                    }
                    skNo.add(skill.getName());
                    plugin.debugLog(Level.INFO, "Skill " + skill.getName() + " Loaded");
                }
            }
        }
        Heroes.log(Level.INFO, "Skills loaded: " + skNo.toString().replace("[", "").replace("]", ""));
    }
}
