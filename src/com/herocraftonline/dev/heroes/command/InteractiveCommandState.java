package com.herocraftonline.dev.heroes.command;

import org.bukkit.command.CommandSender;

public interface InteractiveCommandState {

    public int getMinArguments();

    public int getMaxArguments();

    //public String getIdentifier();
    public boolean isIdentifier(String input);

    public boolean execute(CommandSender executor, String identifier, String[] args);

}
