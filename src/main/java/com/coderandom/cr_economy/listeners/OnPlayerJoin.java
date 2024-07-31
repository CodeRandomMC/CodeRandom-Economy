package com.coderandom.cr_economy.listeners;

import com.coderandom.cr_core.listener.BaseListener;
import com.coderandom.cr_economy.CodeRandomEconomy;
import com.coderandom.cr_economy.EconomyFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin extends BaseListener {
    public OnPlayerJoin() {
        super(CodeRandomEconomy.getInstance());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        EconomyFactory.getInstance().loadBalance(event.getPlayer().getUniqueId());
    }
}
