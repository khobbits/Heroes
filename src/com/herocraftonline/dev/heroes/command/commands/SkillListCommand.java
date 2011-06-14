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
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillListCommand extends BaseCommand {

    private static final int SKILLS_PER_PAGE = 8;

    public SkillListCommand(Heroes plugin) {
        super(plugin);
        name = "Skill";
        description = "Displays a list of your class skills";
        usage = "/skills [page#]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("skills");
        identifiers.add("skilllist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return;
        }
        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        HeroClass heroClass = hero.getHeroClass();

        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
            }
        }

        Map<Skill, Integer> skills = new HashMap<Skill, Integer>();
        // Filter out Skills from the command list.
        for (BaseCommand command : plugin.getCommandManager().getCommands()) {
            if (command instanceof Skill) {
                Skill skill = (Skill) command;
                if (heroClass.hasSkill(skill.getName()) && !skills.containsKey(skill)) {
                    skills.put(skill, skill.getSetting(heroClass, skill.SETTING_LEVEL, 1));
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

        sender.sendMessage(ChatColor.RED + "To use a skill, type " + ChatColor.WHITE + "/skill <name>" + ChatColor.RED + ". For info use " + ChatColor.WHITE + "/skill <name> ?" + ChatColor.RED + ".");
    }

    // The following method is needed to sort the Skills by level order.
    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1;
            }
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}
