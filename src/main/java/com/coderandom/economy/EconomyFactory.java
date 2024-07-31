package com.coderandom.economy;

import com.coderandom.core.CodeRandomCore;
import com.coderandom.core.MySQLManager;
import org.bukkit.plugin.Plugin;

public class EconomyFactory {
    private static volatile EconomyManager instance;

    public static void initialize() {
        if (instance == null) {
            synchronized (EconomyManager.class) {
                if (instance == null) {
                    if (CodeRandomCore.usingMySQL()) {
                        EconomyMySQL economyMySQL = new EconomyMySQL();
                        economyMySQL.createTables();
                        instance = economyMySQL;
                    } else {
                        CodeRandomEconomy.getInstance().getLogger().info("MySQL disabled in CodeRandomCore using JSON!");
                        instance = new EconomyJson();
                    }
                }
            }
        }
    }

    public static EconomyManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EconomyManager has not been initialized. Call initialize() first.");
        }
        return instance;
    }
}
