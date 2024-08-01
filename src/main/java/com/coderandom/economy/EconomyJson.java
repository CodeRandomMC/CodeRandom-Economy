package com.coderandom.economy;

import com.coderandom.core.storage.JsonFileManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EconomyJson implements EconomyManager {
    private static final Logger LOGGER = CodeRandomEconomy.getInstance().getLogger();
    private final JsonFileManager accountsFile;
    private final ConcurrentHashMap<UUID, Double> balanceCache;
    private final double defaultBalance;
    private static final boolean usingAutoSave = VaultEconomy.usingAutoSave();

    EconomyJson() {
        this.accountsFile = new JsonFileManager(CodeRandomEconomy.getInstance(), "DATA", "wallets");
        this.balanceCache = new ConcurrentHashMap<>();
        this.defaultBalance = CodeRandomEconomy.getInstance().getConfig().getDouble("default_balance", 0.0);
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
        double currentBalance = getBalance(uuid);
        setBalance(uuid, currentBalance + amount);
    }

    @Override
    public double loadBalance(UUID uuid) {
        if (usingAutoSave && balanceCache.containsKey(uuid)) {
            return balanceCache.get(uuid);
        }

        CompletableFuture<Double> future = accountsFile.getAsync().thenApply(jsonElement -> {
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has(uuid.toString())) {
                    double balance = jsonObject.get(uuid.toString()).getAsDouble();
                    if (usingAutoSave) {
                        balanceCache.put(uuid, balance);
                    }
                    return balance;
                }
            }
            if (usingAutoSave) {
                balanceCache.put(uuid, defaultBalance); // Default balance if not found
            }
            return defaultBalance;
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Could not load balance for player: " + uuid, throwable);
            return defaultBalance;
        });

        try {
            return future.get();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while loading balance for player: " + uuid, e);
            return defaultBalance;
        }
    }

    @Override
    public void saveBalance(UUID uuid) {
        double balance = getBalance(uuid);
        saveBalance(uuid, balance);
    }

    public void saveBalance(UUID uuid, double balance) {
        accountsFile.getAsync().thenAccept(jsonElement -> {
            JsonObject jsonObject;
            if (jsonElement != null && jsonElement.isJsonObject()) {
                jsonObject = jsonElement.getAsJsonObject();
            } else {
                jsonObject = new JsonObject();
            }

            jsonObject.addProperty(uuid.toString(), balance);
            accountsFile.setAsync(jsonObject).exceptionally(throwable -> {
                LOGGER.log(Level.SEVERE, "Could not save balance for player: " + uuid, throwable);
                return null;
            });
            if (usingAutoSave) {
                balanceCache.remove(uuid);
            }
        });
    }

    @Override
    public void saveAllBalances() {
        if (!usingAutoSave || balanceCache.isEmpty()) return;

        accountsFile.getAsync().thenAccept(jsonElement -> {
            JsonObject jsonObject;
            if (jsonElement != null && jsonElement.isJsonObject()) {
                jsonObject = jsonElement.getAsJsonObject();
            } else {
                jsonObject = new JsonObject();
            }

            // Make a copy of the keys to avoid concurrent modification
            Set<UUID> keys = Set.copyOf(balanceCache.keySet());
            for (UUID uuid : keys) {
                jsonObject.addProperty(uuid.toString(), balanceCache.get(uuid));
            }

            accountsFile.setAsync(jsonObject).exceptionally(throwable -> {
                LOGGER.log(Level.SEVERE, "Could not save all balances!", throwable);
                return null;
            });

            balanceCache.clear();
            LOGGER.log(Level.INFO, "Saved all cached balances.");
        });
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        CompletableFuture<Boolean> future = accountsFile.getAsync().thenApply(jsonElement -> {
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject.has(uuid.toString());
            }
            return false;
        }).exceptionally(throwable -> {
            LOGGER.log(Level.SEVERE, "Could not check if account exists for player: " + uuid, throwable);
            return false;
        });

        try {
            return future.get();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while checking account for player: " + uuid, e);
            return false;
        }
    }
}
