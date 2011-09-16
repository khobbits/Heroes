package com.herocraftonline.economy.economies;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class EssE implements com.herocraftonline.economy.Economy {

    private Essentials econ;
    
    @Override
    public String getName() {
        return "Essentials";
    }

    @Override
    public Plugin getPlugin() {
        return econ;
    }

    @Override
    public double getHoldings(String name) {
        try {
            return Economy.getMoney(name);
        } catch (UserDoesNotExistException e) {
            System.out.println("[Essentials] Failed to grab economy balance for: " + name);
        }
        
        return 0.0;
    }

    @Override
    public double withdraw(String name, double amount) {
        double initial = amount;
        if (getHoldings(name) < amount)
            amount = getHoldings(name);
        
        try {
            Economy.subtract(name, amount);
        } catch (UserDoesNotExistException e) {
            System.out.println("[Essentials] Failed to grab economy balance for: " + name);
            return initial;
        } catch (NoLoanPermittedException e) {
            System.out.println("[Essentials] No Loan Permitted in economy for: " + name);
            return initial;
        }
        
        return initial - amount;
    }

    @Override
    public boolean deposit(String name, double amount) {
        try {
            Economy.add(name, amount);
        } catch (UserDoesNotExistException e) {
            System.out.println("[Essentials] Failed to grab economy balance for: " + name);
            return false;
        } catch (NoLoanPermittedException e) {
            System.out.println("[Essentials] No Loan Permitted in economy for: " + name);
            return false;
        }
        return true;
    }

    @Override
    public boolean has(String name, double amount) {
        try {
            return Economy.hasEnough(name, amount);
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public boolean transfer(String from, String to, double amount) {
        if (!has(from, amount) || !Economy.playerExists(to))
            return false;
        
        //Try the deposit
        if (!deposit(to, amount))
            return false;
        
        //Try the Withdraw
        withdraw(from, amount);
        return true;
    }

    @Override
    public void setPlugin(Plugin plugin) {
        this.econ = (Essentials) plugin;
    }

    @Override
    public String format(double amount) {
        return Economy.format(amount);
    }

}
