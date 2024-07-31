package com.coderandom.cr_economy;

import com.coderandom.cr_core.MySQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EconomyMySQL implements EconomyManager {
    private static final Logger LOGGER = CodeRandomEconomy.getInstance().getLogger();
    private final MySQLManager mySQLManager;
    private final HashMap<UUID, Double> balanceCache;

    public EconomyMySQL() {
        this.mySQLManager = MySQLManager.getInstance();
        this.balanceCache = new HashMap<>();
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
        if (balanceCache.containsKey(uuid)) {
            return balanceCache.get(uuid);
        }
        return loadBalance(uuid);
    }

    @Override
    public void setBalance(UUID uuid, double balance) {
        balanceCache.put(uuid, balance);
    }

    @Override
    public void updateBalance(UUID uuid, double amount) {
        double newBalance = getBalance(uuid) + amount;
        setBalance(uuid, newBalance);
    }

    @Override
    public double loadBalance(UUID uuid) {
        String query = "SELECT balance FROM player_balances WHERE uuid = ?";
        try (ResultSet rs = mySQLManager.executeQuery(query, uuid.toString())) {
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceCache.put(uuid, balance);
                return balance;
            } else {
                balanceCache.put(uuid, 0.0); // Default balance if not found
                return 0.0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not load balance for player: " + uuid, e);
            return 0.0;
        }
    }

    @Override
    public void saveBalance(UUID uuid) {
        double balance = getBalance(uuid);
        String query = "REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";
        try {
            mySQLManager.executeUpdate(query, uuid.toString(), balance);
            balanceCache.remove(uuid);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not save balance for player: " + uuid, e);
        }
    }

    @Override
    public void saveAllBalances() {
        for (UUID uuid : balanceCache.keySet()) {
            saveBalance(uuid);
        }
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
