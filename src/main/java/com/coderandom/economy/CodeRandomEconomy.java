package com.coderandom.economy;

import com.coderandom.core.CodeRandomCore;
import com.coderandom.economy.commands.BalanceCommand;
import com.coderandom.economy.commands.PayCommand;
import com.coderandom.economy.listeners.OnPlayerJoinListener;
import com.coderandom.economy.listeners.OnPlayerQuitListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class CodeRandomEconomy extends JavaPlugin {
    private static CodeRandomEconomy instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Check if CodeRandom-Core installed
        if (getServer().getPluginManager().getPlugin("CodeRandomCore") == null){
            getLogger().severe("CodeRandomCore not found! Disabling CodeRandomEconomy...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check if Vault installed
        if (!CodeRandomCore.getInstance().dependencyRequirement(this, "Vault")) return;

        // Setup Vault Economy
        setupEconomy();

        getLogger().log(Level.INFO, "CodeRandomEconomy enabled successfully.");
        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        new OnPlayerJoinListener();
        new OnPlayerQuitListener();
    }

    private void registerCommands() {
        new BalanceCommand();
        new PayCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().log(Level.INFO, "CodeRandomEconomy disabled successfully.");
        if (getServer().getPluginManager().getPlugin("CodeRandomCore") == null) return;
        if (CodeRandomCore.getInstance().dependencyCheck("Vault")) {
            EconomyFactory.getInstance().saveAllBalances();
        }
    }

    private void setupEconomy() {
        // Check if another Economy provider is already registered
        if (CodeRandomCore.getInstance().getEconomy() != null) {
            getLogger().severe("Another economy plugin is already loaded. Disabling " + this.getName() + ".");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register your economy as the primary provider
        getServer().getServicesManager().register(Economy.class, new VaultEconomy(this), this, ServicePriority.Highest);
        getLogger().info(this.getName() + " has been successfully registered as the primary economy provider.");

    }

    public static CodeRandomEconomy getInstance() {
        return instance;
    }
}
