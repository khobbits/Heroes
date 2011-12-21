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
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillListCommand extends BasicCommand {

    private static final int SKILLS_PER_PAGE = 8;
    private final Heroes plugin;

    public SkillListCommand(Heroes plugin) {
        super("List Skills");
        this.plugin = plugin;
        setDescription("Displays a list of your class skills");
        setUsage("/skills §8<prim|prof|heroclass> [page#]");
        setArgumentRange(0, 2);
        setIdentifiers("skills", "hero skills");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        HeroClass heroClass = hero.getHeroClass();
        HeroClass secondClass = hero.getSecondClass();

        int page = 0;
        boolean prim = true;
        boolean sec = true;
        HeroClass hc = null;
        String title = "";
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
                title = "Your Class(es)";
            } catch (NumberFormatException e) {
                if (args[0].toLowerCase().contains("prim")) {
                    sec = false;
                    title = heroClass.getName();
                } else if (args[0].toLowerCase().contains("pro")) {
                    prim = false;
                    if (secondClass == null) {
                        Messaging.send(sender, "You don't have a secondary class!");
                        return true;
                    }
                    title = secondClass.getName();
                // If we have 2 arguments lets try to get the page
                } else {
                    hc = plugin.getClassManager().getClass(args[0]);
                    if (hc != null) {
                        prim = false;
                        sec = false;
                        title = hc.getName();
                    } else {
                        Messaging.send(sender, "That class does not exist!");
                        return true;
                    }
                }
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]) -1;
                    } catch (NumberFormatException f) {
                        // Ignore second exception
                    }
                }
            }
        }

        Map<SkillListInfo, Integer> skills = new HashMap<SkillListInfo, Integer>();
        if (prim) {
            for (String name : heroClass.getSkillNames()) {
                Skill skill = plugin.getSkillManager().getSkill(name);
                int level = SkillConfigManager.getSetting(heroClass, skill, Setting.LEVEL.node(), 1);
                skills.put(new SkillListInfo(heroClass, skill), level);
            }
        }
        if (sec && secondClass != null) {
            for (String name : secondClass.getSkillNames()) {
                Skill skill = plugin.getSkillManager().getSkill(name);
                int level = SkillConfigManager.getSetting(secondClass, skill, Setting.LEVEL.node(), 1);
                skills.put(new SkillListInfo(secondClass, skill), level);
            }
        }
        if (hc != null) {
            for (String name : hc.getSkillNames()) {
                Skill skill = plugin.getSkillManager().getSkill(name);
                int level = SkillConfigManager.getSetting(hc, skill, Setting.LEVEL.node(), 1);
                skills.put(new SkillListInfo(hc, skill), level);
            }
        }
        int numPages = skills.size() / SKILLS_PER_PAGE;
        if (skills.size() % SKILLS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }

        sender.sendMessage(ChatColor.RED + "-----[ " + ChatColor.WHITE + title + " Skills <" + (page + 1) + "/" + numPages + ">" + ChatColor.RED + " ]-----");
        int start = page * SKILLS_PER_PAGE;
        int end = start + SKILLS_PER_PAGE;
        if (end > skills.size()) {
            end = skills.size();
        }

        int count = 0;

        for (Entry<SkillListInfo, Integer> entry : entriesSortedByValues(skills)) {
            if (count >= start && count < end) {
                SkillListInfo sli = entry.getKey();
                int level = entry.getValue();
                hc = sli.heroClass;
                ChatColor color;
                if (level > hero.getLevel(hc)) {
                    color = ChatColor.RED;
                } else {
                    color = ChatColor.GREEN;
                }
                sender.sendMessage("  " + color + " " + hc.getName().substring(0, 3 > hc.getName().length() ? hc.getName().length() : 3) + " " + level + " " + ChatColor.YELLOW + sli.skill.getName() + ": " + ChatColor.GOLD + sli.skill.getDescription());
            }
            count++;
        }

        sender.sendMessage(ChatColor.RED + "To use a skill, type " + ChatColor.WHITE + "/skill <name>" + ChatColor.RED + ". For info use " + ChatColor.WHITE + "/skill <name> ?");
        return true;
    }

    private static SortedSet<Entry<SkillListInfo, Integer>> entriesSortedByValues(Map<SkillListInfo, Integer> map) {
        SortedSet<Entry<SkillListInfo, Integer>> sortedEntries = new TreeSet<Map.Entry<SkillListInfo, Integer>>(new Comparator<Map.Entry<SkillListInfo, Integer>>() {
            @Override
            public int compare(Map.Entry<SkillListInfo, Integer> e1, Map.Entry<SkillListInfo, Integer> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                if (res == 0)
                    return e1.getKey().skill.getName().compareTo(e2.getKey().skill.getName());
                else
                    return res;
            }
        });

        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public class SkillListInfo {

        protected final HeroClass heroClass;
        protected final Skill skill;

        public SkillListInfo(HeroClass heroClass, Skill skill) {
            this.heroClass = heroClass;
            this.skill = skill;
        }

        public int hashCode() {
            return 3 + heroClass.hashCode() * 17 + skill.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (!(obj instanceof SkillListInfo))
                return false;

            SkillListInfo sli = (SkillListInfo) obj;
            return sli.heroClass.equals(this.heroClass) && sli.skill.equals(this.skill);
        }
    }
}
