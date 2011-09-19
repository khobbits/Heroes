package com.herocraftonline.dev.heroes.command;

import org.bukkit.command.CommandSender;

public interface InteractiveCommandState {

    public boolean execute(CommandSender executor, String identifier, String[] args);

    public int getMaxArguments();

    public int getMinArguments();

    // public String getIdentifier();
    public boolean isIdentifier(String input);

}
