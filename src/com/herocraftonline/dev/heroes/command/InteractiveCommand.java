package com.herocraftonline.dev.heroes.command;

import org.bukkit.command.CommandSender;

public interface InteractiveCommand extends Command {

    public String getCancelIdentifier();

    public void onCommandCancelled(CommandSender executor);

}
