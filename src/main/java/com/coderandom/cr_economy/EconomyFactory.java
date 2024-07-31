package com.coderandom.cr_economy;

import com.coderandom.cr_core.CRCore;
import com.coderandom.cr_core.MySQLManager;
import org.bukkit.plugin.Plugin;

public class EconomyFactory {
    private static volatile EconomyManager instance;

    public static void initialize(Plugin plugin) {
        if (instance == null) {
            synchronized (EconomyManager.class) {
                if (instance == null) {
                    if (CRCore.usingMySQL()) {
                        MySQLManager mySQLManager = MySQLManager.getInstance();
                        if (mySQLManager.connect()) {
                            EconomyMySQL economyMySQL = new EconomyMySQL();
                            economyMySQL.createTables();
                            instance = economyMySQL;
                        } else {
                            plugin.getLogger().severe("Failed to connect to MySQL database. Defaulting to JSON.");
                            instance = new EconomyJson();
                        }
                    } else {
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
