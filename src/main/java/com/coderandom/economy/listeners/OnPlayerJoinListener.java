package com.coderandom.economy.listeners;

import com.coderandom.core.listener.BaseListener;
import com.coderandom.economy.CodeRandomEconomy;
import com.coderandom.economy.EconomyFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public final class OnPlayerJoinListener extends BaseListener {
    public OnPlayerJoinListener() {
        super(CodeRandomEconomy.getInstance());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        EconomyFactory.getInstance().getBalance(event.getPlayer().getUniqueId());
    }
}
