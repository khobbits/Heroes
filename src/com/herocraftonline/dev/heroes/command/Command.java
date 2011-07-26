package com.herocraftonline.dev.heroes.command;

import org.bukkit.command.CommandSender;

public interface Command {

    public String getName();
    public String getDescription();
    public String getUsage();
    public String getPermission();
    public String[] getNotes();
    public String[] getIdentifiers();
    public int getMinArguments();
    public int getMaxArguments();
    public void cancelInteraction(CommandSender executor);
    public boolean isInProgress(CommandSender executor);
    public boolean isShownOnHelpMenu();
    public boolean isInteractive();
    public boolean isIdentifier(CommandSender executor, String input);
    public boolean execute(CommandSender executor, String identifier, String[] args);
    
}
