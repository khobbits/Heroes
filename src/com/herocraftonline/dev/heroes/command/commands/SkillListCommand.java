package com.herocraftonline.dev.heroes.command.commands;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.command.Command;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillListCommand extends BasicCommand {

    private static final int SKILLS_PER_PAGE = 8;
    private final Heroes plugin;

    public SkillListCommand(Heroes plugin) {
        super("List Skills");
        this.plugin = plugin;
        setDescription("Displays a list of your class skills");
        setUsage("/skills §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skills", "hero skills" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        HeroClass heroClass = hero.getHeroClass();

        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {}
        }

        Map<Skill, Integer> skills = new HashMap<Skill, Integer>();
        // Filter out Skills from the command list.
        for (Command command : plugin.getCommandHandler().getCommands()) {
            if (command instanceof Skill) {
                Skill skill = (Skill) command;
                if (heroClass.hasSkill(skill.getName()) && !skills.containsKey(skill)) {
                    skills.put(skill, skill.getSetting(heroClass, Skill.SETTING_LEVEL, 1));
                }
            }
        }

        int numPages = skills.size() / SKILLS_PER_PAGE;
        if (skills.size() % SKILLS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }

        sender.sendMessage(ChatColor.RED + "-----[ " + ChatColor.WHITE + heroClass.getName() + " Skills <" + (page + 1) + "/" + numPages + ">" + ChatColor.RED + " ]-----");
        int start = page * SKILLS_PER_PAGE;
        int end = start + SKILLS_PER_PAGE;
        if (end > skills.size()) {
            end = skills.size();
        }

        int count = 0;

        for (Entry<Skill, Integer> entry : entriesSortedByValues(skills)) {
            if (count >= start && count < end) {
                Skill skill = entry.getKey();
                int level = entry.getValue();
                ChatColor color;
                if (level > hero.getLevel()) {
                    color = ChatColor.RED;
                } else {
                    color = ChatColor.GREEN;
                }
                sender.sendMessage("  " + color + "Level " + level + " " + ChatColor.YELLOW + skill.getName() + ": " + ChatColor.GOLD + skill.getDescription());
            }
            count++;
        }

        sender.sendMessage(ChatColor.RED + "To use a skill, type " + ChatColor.WHITE + "/skill <name>" + ChatColor.RED + ". For info use " + ChatColor.WHITE + "/skill <name> ?");
        return true;
    }

    private static SortedSet<Entry<Skill, Integer>> entriesSortedByValues(Map<Skill, Integer> map) {
        SortedSet<Entry<Skill, Integer>> sortedEntries = new TreeSet<Map.Entry<Skill, Integer>>(new Comparator<Map.Entry<Skill, Integer>>() {
            @Override
            public int compare(Map.Entry<Skill, Integer> e1, Map.Entry<Skill, Integer> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                if (res == 0) {
                    return e1.getKey().getName().compareTo(e2.getKey().getName());
                } else {
                    return res;
                }
            }
        });

        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
