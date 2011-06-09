package com.herocraftonline.dev.heroes.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;

public abstract class BaseCommand {

    protected Heroes plugin;
    protected String name;
    protected String description;
    protected String usage;
    protected String permissionNode;
    protected int minArgs;
    protected int maxArgs;
    protected List<String> identifiers;
    protected List<String> notes;

    public BaseCommand(Heroes plugin) {
        this.plugin = plugin;
        this.identifiers = new ArrayList<String>();
        this.notes = new ArrayList<String>();
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String[] validate(String input, StringBuilder identifier) {
        String match = matchIdentifier(input);

        if (match != null) {
            identifier = identifier.append(match);
            int i = identifier.length();
            String[] args = input.substring(i).trim().split(" ");
            if (args[0].isEmpty()) {
                args = new String[0];
            }
            int l = args.length;
            if (l >= minArgs && l <= maxArgs) {
                return args;
            }
        }
        return null;
    }

    public String matchIdentifier(String input) {
        String lower = input.toLowerCase();

        int index = -1;
        int n = identifiers.size();
        for (int i = 0; i < n; i++) {
            String identifier = identifiers.get(i).toLowerCase();
            if (lower.matches(identifier + "(\\s+.*|\\s*)")) {
                index = i;
            }
        }

        if (index != -1) {
            return identifiers.get(index);
        } else {
            return null;
        }
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == this.getClass()) {
            return false;
        }
        BaseCommand other = (BaseCommand) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public List<String> getNotes() {
        return notes;
    }

}
