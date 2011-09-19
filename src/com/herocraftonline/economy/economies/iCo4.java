package com.herocraftonline.economy.economies;

import org.bukkit.plugin.Plugin;

import com.herocraftonline.economy.Economy;
import com.nijiko.coelho.iConomy.iConomy;

public class iCo4 implements Economy {

    private iConomy econ;

    @Override
    public boolean deposit(String name, double amount) {
        iConomy.getBank().getAccount(name).add(amount);
        return true;
    }

    @Override
    public String format(double amount) {
        return iConomy.getBank().format(amount);
    }

    @Override
    public double getHoldings(String name) {
        return iConomy.getBank().getAccount(name).getBalance();
    }

    @Override
    public String getName() {
        return "iCo4";
    }

    @Override
    public Plugin getPlugin() {
        return this.econ;
    }

    @Override
    public boolean has(String name, double amount) {
        return getHoldings(name) >= amount;
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
        iConomy.getBank().getAccount(name).subtract(amount);

        return initial - amount;
    }

}
