package com.herocraftonline.economy.economies;

import org.bukkit.plugin.Plugin;

import com.herocraftonline.economy.Economy;
import com.iCo6.iConomy;
import com.iCo6.system.Accounts;

public class iCo6 implements Economy {

    private iConomy econ;
    private Accounts accounts;
    
    public iCo6() {
        accounts = new Accounts();
    }
    
    @Override
    public String getName() {
        return "iCo6";
    }

    @Override
    public Plugin getPlugin() {
        return econ;
    }

    @Override
    public double getHoldings(String name) {
        return accounts.get(name).getHoldings().getBalance();
    }

    @Override
    public double withdraw(String name, double amount) {
        double initial = amount;
        if (getHoldings(name) < amount)
            amount = getHoldings(name);
        
        accounts.get(name).getHoldings().subtract(amount);
        return initial - amount;
    }

    @Override
    public boolean deposit(String name, double amount) {
        accounts.get(name).getHoldings().add(amount);
        return true;
    }

    @Override
    public boolean has(String name, double amount) {
        return accounts.get(name).getHoldings().hasEnough(amount);
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
        this.econ = (iConomy) plugin;
    }

    @Override
    public String format(double amount) {
        return iConomy.format(amount);
    }
}
