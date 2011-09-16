package com.herocraftonline.economy.economies;

import org.bukkit.plugin.Plugin;

import com.herocraftonline.economy.Economy;

import cosine.boseconomy.BOSEconomy;

public class BOSE implements Economy {

    private BOSEconomy econ;
    
    @Override
    public String getName() {
        return "BOSE";
    }

    @Override
    public Plugin getPlugin() {
        return econ;
    }

    @Override
    public double getHoldings(String name) {
        return econ.getPlayerMoney(name);
    }

    @Override
    public double withdraw(String name, double amount) {
        double initial = amount;
        if (getHoldings(name) < amount)
            amount = getHoldings(name);
        econ.setPlayerMoney(name, (int) (getHoldings(name) - amount), false);
        
        return initial - amount;
    }

    @Override
    public boolean deposit(String name, double amount) {
        econ.addPlayerMoney(name, (int) amount, false);
        return true;
    }

    @Override
    public boolean has(String name, double amount) {
        return getHoldings(name) >= amount;
            
    }

    @Override
    public boolean transfer(String from, String to, double amount) {
        if (!has(from, amount))
            return false;
        
        withdraw(from, amount);
        deposit(to, amount);
        return true;
    }

    @Override
    public void setPlugin(Plugin plugin) {
        this.econ = (BOSEconomy) plugin;
    }

    @Override
    public String format(double amount) {
        return amount + " " + econ.getMoneyNamePlural();
    }

}
