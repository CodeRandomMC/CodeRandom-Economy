package com.coderandom.cr_economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class CodeRandomEconomy extends JavaPlugin {
    private static CodeRandomEconomy instance;
    private static Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Setup Vault Economy
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Vault dependency not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().log(Level.INFO, "CodeRandomEconomy enabled successfully.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().log(Level.INFO, "CodeRandomEconomy disabled successfully.");
        EconomyFactory.getInstance().saveAllBalances();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().log(Level.SEVERE, "Vault plugin not found!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().log(Level.SEVERE, "No Economy provider found! Registering VaultEconomyManager.");
            economy = new VaultEconomy(this);
            getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Highest);
        } else {
            economy = rsp.getProvider();
        }

        return economy != null;
    }

    public static CodeRandomEconomy getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
