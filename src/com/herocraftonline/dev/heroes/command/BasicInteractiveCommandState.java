package com.herocraftonline.dev.heroes.command;

public abstract class BasicInteractiveCommandState implements InteractiveCommandState {

    private String identifier;
    private int minArguments = 0;
    private int maxArguments = 0;

    public BasicInteractiveCommandState(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getMinArguments() {
        return minArguments;
    }

    @Override
    public int getMaxArguments() {
        return maxArguments;
    }

    public void setArgumentRange(int min, int max) {
        this.minArguments = min;
        this.maxArguments = max;
    }

    /*
     * @Override
     * public String getIdentifier() {
     * return identifier;
     * }
     */

    @Override
    public boolean isIdentifier(String input) {
        return input.equalsIgnoreCase(identifier);
    }

}
