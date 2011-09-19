package com.herocraftonline.economy.economies;

import org.bukkit.plugin.Plugin;

import com.herocraftonline.economy.Economy;
import com.iConomy.iConomy;

public class iCo5 implements Economy {

    private iConomy econ;

    @Override
    public boolean deposit(String name, double amount) {
        iConomy.getAccount(name).getHoldings().add(amount);
        return true;
    }

    @Override
    public String format(double amount) {
        return iConomy.format(amount);
    }

    @Override
    public double getHoldings(String name) {
        return iConomy.getAccount(name).getHoldings().balance();
    }

    @Override
    public String getName() {
        return "iCo5";
    }

    @Override
    public Plugin getPlugin() {
        return this.econ;
    }

    @Override
    public boolean has(String name, double amount) {
        return iConomy.getAccount(name).getHoldings().hasEnough(amount);
    }

    @Override
    public void setPlugin(Plugin plugin) {
        this.econ = (iConomy) plugin;
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
    public double withdraw(String name, double amount) {
        double initial = amount;
        double holdings = getHoldings(name);

        if (holdings < amount) {
            amount = holdings;
        }
        iConomy.getAccount(name).getHoldings().subtract(amount);

        return initial - amount;
    }

}
