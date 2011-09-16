package com.herocraftonline.economy;

import org.bukkit.plugin.Plugin;

public interface Economy {
    
    public String getName();
    
    public void setPlugin(Plugin plugin);
    
    public Plugin getPlugin();
    
    public double getHoldings(String name);
    
    public double withdraw(String name, double amount);
    
    public boolean deposit(String name, double amount);
    
    public boolean has(String name, double amount);
    
    public boolean transfer(String from, String to, double amount);
    
    public String format(double amount);
}
