package com.herocraftonline.economy;

import org.bukkit.plugin.Plugin;

public interface Economy {

    public boolean deposit(String name, double amount);

    public String format(double amount);

    public double getHoldings(String name);

    public String getName();

    public Plugin getPlugin();

    public boolean has(String name, double amount);

    public void setPlugin(Plugin plugin);

    public boolean transfer(String from, String to, double amount);

    public double withdraw(String name, double amount);
}
