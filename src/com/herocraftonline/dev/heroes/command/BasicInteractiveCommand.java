package com.herocraftonline.dev.heroes.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dev.heroes.util.Messaging;

public abstract class BasicInteractiveCommand extends BasicCommand implements InteractiveCommand {

    private InteractiveCommandState[] states = new InteractiveCommandState[0];
    private Map<CommandSender, Integer> userStates = new HashMap<CommandSender, Integer>();

    public BasicInteractiveCommand(String name) {
        super(name);
    }

    public void setStates(InteractiveCommandState[] states) {
        this.states = states;
        
        int minArgs = Integer.MAX_VALUE;
        int maxArgs = Integer.MIN_VALUE;
        for (InteractiveCommandState state : states) {
            if (state.getMinArguments() < minArgs) {
                minArgs = state.getMinArguments();
            }

            if (state.getMaxArguments() > maxArgs) {
                maxArgs = state.getMaxArguments();
            }
        }
        this.setArgumentRange(minArgs, maxArgs);
        
        String[] identifiers = new String[states.length + 1];
        for (int i = 0; i < states.length; i++) {
            identifiers[i] = states[i].getIdentifier();
        }
        identifiers[states.length] = this.getCancellationIdentifier();
        this.setIdentifiers(identifiers);
    }

    @Override
    public boolean isInProgress(CommandSender executor) {
        return userStates.containsKey(executor);
    }

    @Override
    public boolean isIdentifier(CommandSender executor, String input) {
        if (input.equalsIgnoreCase(this.getCancellationIdentifier())) {
            return true;
        }

        for (String identifier : getIdentifiers()) {
            if (input.equalsIgnoreCase(identifier)) {
                for (InteractiveCommandState state : states) {
                    if (state.isIdentifier(input)) {
                        if (state == states[0] || userStates.containsKey(executor)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean execute(CommandSender executor, String identifier, String[] args) {
        if (states.length == 0) {
            return true;
        }

        int stateIndex = 0;
        if (userStates.containsKey(executor)) {
            stateIndex = userStates.get(executor);
            if (this.getCancellationIdentifier().equals(identifier)) {
                Messaging.send(executor, "Exiting command.");
                userStates.remove(executor);
                return true;
            }
        }

        InteractiveCommandState state = states[stateIndex];
        if (!state.isIdentifier(identifier)) {
            return true;
        } else if (args.length < state.getMinArguments() || args.length > state.getMaxArguments() || !state.execute(executor, identifier, args)) {
            if (stateIndex == 0) {
                Messaging.send(executor, "Invalid input.");
            } else {
                Messaging.send(executor, "Invalid input - try again or type $1 to start over.", "/" + this.getCancellationIdentifier());
            }
        } else {
            stateIndex++;
            if (states.length > stateIndex) {
                userStates.put(executor, stateIndex++);
            } else {
                userStates.remove(executor);
            }
        }

        return true;
    }

}
