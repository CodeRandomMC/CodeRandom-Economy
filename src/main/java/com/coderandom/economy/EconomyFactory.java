package com.coderandom.economy;

import com.coderandom.core.CodeRandomCore;

public final class EconomyFactory {
    private static volatile EconomyManager instance;

    static void initialize() {
        if (instance == null) {
            synchronized (EconomyManager.class) {
                if (instance == null) {
                    if (!CodeRandomEconomy.getInstance().getConfig().getBoolean("MySQL", false)) {
                        CodeRandomEconomy.getInstance().getLogger().info("MySQL disabled in CodeRandomEconomy using JSON!");
                        instance = new EconomyJson();
                        return;
                    }
                    if (CodeRandomCore.usingMySQL()) {
                        CodeRandomEconomy.getInstance().getLogger().info("MySQL enabled and connected!");
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
