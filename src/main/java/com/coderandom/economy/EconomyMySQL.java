package com.coderandom.economy;

import com.coderandom.core.CodeRandomCore;
import com.coderandom.core.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EconomyMySQL implements EconomyManager {
    private static final Logger LOGGER = CodeRandomEconomy.getInstance().getLogger();
    private final MySQLManager mySQLManager;
    private final ConcurrentHashMap<UUID, Double> balanceCache;
    private final double defaultBalance;
    private static final boolean usingAutoSave = VaultEconomy.usingAutoSave();

    public EconomyMySQL() {
        this.mySQLManager = CodeRandomCore.getMySQLManager();
        this.balanceCache = new ConcurrentHashMap<>();
        this.defaultBalance = CodeRandomEconomy.getInstance().getConfig().getDouble("default_balance", 0.0);
    }

    public void createTables() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS player_balances (" +
                "uuid VARCHAR(36) NOT NULL," +
                "balance DOUBLE NOT NULL," +
                "PRIMARY KEY (uuid))";

        mySQLManager.createTables(createTableQuery);
    }

    @Override
    public double getBalance(UUID uuid) {
        if (!usingAutoSave) {
            return loadBalance(uuid);
        } else if (balanceCache.containsKey(uuid)) {
            return balanceCache.get(uuid);
        }
        return loadBalance(uuid);
    }

    @Override
    public void setBalance(UUID uuid, double balance) {
        if (!usingAutoSave) {
            saveBalance(uuid, balance);
        } else {
            balanceCache.put(uuid, balance);
        }
    }

    @Override
    public void updateBalance(UUID uuid, double amount) {
        double newBalance = getBalance(uuid) + amount;
        setBalance(uuid, newBalance);
    }

    @Override
    public double loadBalance(UUID uuid) {
        if (usingAutoSave && balanceCache.containsKey(uuid)) {
            return getBalance(uuid);
        }

        String query = "SELECT balance FROM player_balances WHERE uuid = ?";
        try (ResultSet rs = mySQLManager.executeQuery(query, uuid.toString())) {
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (usingAutoSave) {
                    balanceCache.put(uuid, balance);
                }
                return balance;
            } else {
                if (usingAutoSave) {
                    balanceCache.put(uuid, defaultBalance);
                }
                return defaultBalance;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not load balance for player: " + uuid, e);
            return 0.0;
        }
    }

    @Override
    public void saveBalance(UUID uuid) {
        double balance = getBalance(uuid);
        saveBalance(uuid, balance);
    }

    public void saveBalance(UUID uuid, double balance) {
        String query = "REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";
        try {
            mySQLManager.executeUpdate(query, uuid.toString(), balance);
            if (usingAutoSave) {
                balanceCache.remove(uuid);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not save balance for player: " + uuid, e);
        }
    }

    @Override
    public void saveAllBalances() {
        if (!usingAutoSave || balanceCache.isEmpty()) return;
        Set<UUID> keys = new HashSet<>(balanceCache.keySet());
        for (UUID uuid : keys) {
            saveBalance(uuid);
        }
        LOGGER.log(Level.INFO, "Saved all cached balances.");
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        String query = "SELECT 1 FROM player_balances WHERE uuid = ?";
        try (ResultSet rs = mySQLManager.executeQuery(query, uuid.toString())) {
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not check if account exists for player: " + uuid, e);
            return false;
        }
    }
}
