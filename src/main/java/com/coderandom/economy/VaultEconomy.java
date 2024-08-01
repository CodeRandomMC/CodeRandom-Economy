package com.coderandom.economy;

import com.coderandom.core.UUIDFetcher;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public final class VaultEconomy extends AbstractEconomy {
    private static volatile VaultEconomy instance;
    private final Plugin plugin;
    private final EconomyManager economy;

    public VaultEconomy(Plugin plugin) {
        this.plugin = plugin;
        EconomyFactory.initialize();
        economy = EconomyFactory.getInstance();
        instance = this;
    }

    public static VaultEconomy getInstance() {
        if (instance == null) {
            throw new IllegalStateException("VaultEconomy is not initialized. this shouldn't happen.");
        }
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return "CodeRandomEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("$%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    private UUID getPlayerUUID(String playerName) {
        return UUIDFetcher.getUUID(playerName);
    }

    @Override
    public boolean hasAccount(String playerName) {
        UUID uuid = getPlayerUUID(playerName);
        return uuid != null && economy.hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        UUID uuid = getPlayerUUID(playerName);
        return uuid != null ? economy.getBalance(uuid) : 0.0;
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        UUID uuid = getPlayerUUID(playerName);
        if (uuid == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        }
        double balance = economy.getBalance(uuid);
        if (balance < amount) {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
        economy.updateBalance(uuid, -amount);
        return new EconomyResponse(amount, economy.getBalance(uuid), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        UUID uuid = getPlayerUUID(playerName);
        if (uuid == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        }
        economy.updateBalance(uuid, amount);
        return new EconomyResponse(amount, economy.getBalance(uuid), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        UUID uuid = getPlayerUUID(playerName);
        if (uuid != null) {
            if (!economy.hasAccount(uuid)) {
                economy.setBalance(uuid, 0.0);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
}
