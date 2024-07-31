package com.coderandom.economy.listeners;

import com.coderandom.core.listener.BaseListener;
import com.coderandom.economy.CodeRandomEconomy;
import com.coderandom.economy.EconomyFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitListener extends BaseListener {
    public OnPlayerQuitListener() {
        super(CodeRandomEconomy.getInstance());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        EconomyFactory.getInstance().saveBalance(event.getPlayer().getUniqueId());
    }
}
