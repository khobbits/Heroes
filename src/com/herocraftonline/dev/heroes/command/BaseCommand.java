package com.herocraftonline.dev.heroes.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.Heroes;

public abstract class BaseCommand {

    protected Heroes plugin;
    private String name = "Default Name";
    private String description = "Default Description";
    private String usage = "Default Usage";
    private String permissionNode = "";
    private int minArgs = 0;
    private int maxArgs = 0;
    private List<String> identifiers;
    private List<String> notes;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public List<String> getNotes() {
        return notes;
    }

    public String getPermissionNode() {
        return permissionNode;
    }

    public void setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
    }

}
