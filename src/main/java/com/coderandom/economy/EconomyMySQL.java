package com.coderandom.economy;

import com.coderandom.core.CodeRandomCore;
import com.coderandom.core.MySQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
        if (!usingAutoSave || !balanceCache.containsKey(uuid)) {
            return loadBalance(uuid);
        }
        return balanceCache.get(uuid);
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
            return balanceCache.get(uuid);
        }

        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT balance FROM player_balances WHERE uuid = ?";
            try (Connection connection = mySQLManager.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
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
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not load balance for player: " + uuid, e);
                return defaultBalance;
            }
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Exception while loading balance for player: " + uuid, throwable);
            return defaultBalance;
        }).join();
    }

    @Override
    public void saveBalance(UUID uuid) {
        double balance = getBalance(uuid);
        saveBalance(uuid, balance);
    }

    public void saveBalance(UUID uuid, double balance) {
        CompletableFuture.runAsync(() -> {
            String query = "REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";
            try {
                mySQLManager.executeUpdate(query, uuid.toString(), balance);
                if (usingAutoSave) {
                    balanceCache.remove(uuid);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not save balance for player: " + uuid, e);
            }
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Exception while saving balance for player: " + uuid, throwable);
            return null;
        });
    }

    @Override
    public void saveAllBalances() {
        if (!usingAutoSave || balanceCache.isEmpty()) return;

        CompletableFuture.runAsync(() -> {
            String query = "REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";
            try {
                mySQLManager.executeBatchUpdate(query, balanceCache.entrySet().stream()
                        .map(entry -> new Object[]{entry.getKey().toString(), entry.getValue()})
                        .toArray(Object[][]::new));
                balanceCache.clear();
                LOGGER.log(Level.INFO, "Saved all cached balances.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not save all balances!", e);
            }
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Exception while saving all balances!", throwable);
            return null;
        });
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT 1 FROM player_balances WHERE uuid = ?";
            try (Connection connection = mySQLManager.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not check if account exists for player: " + uuid, e);
                return false;
            }
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Exception while checking account for player: " + uuid, throwable);
            return false;
        }).join();
    }
}
