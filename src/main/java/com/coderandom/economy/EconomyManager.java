package com.coderandom.economy;

import java.util.UUID;

public interface EconomyManager {
    double getBalance(UUID uuid);
    void setBalance(UUID uuid, double balance);
    void updateBalance(UUID uuid, double amount);
    double loadBalance(UUID uuid);
    void saveBalance(UUID uuid);
    void saveAllBalances();
    boolean hasAccount(UUID playerUUID);
}

