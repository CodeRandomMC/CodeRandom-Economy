package com.coderandom.cr_economy.listeners;

import com.coderandom.cr_core.listener.BaseListener;
import com.coderandom.cr_economy.CodeRandomEconomy;
import com.coderandom.cr_economy.EconomyFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuit extends BaseListener {
    public OnPlayerQuit() {
        super(CodeRandomEconomy.getInstance());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        EconomyFactory.getInstance().saveBalance(event.getPlayer().getUniqueId());
    }
}
