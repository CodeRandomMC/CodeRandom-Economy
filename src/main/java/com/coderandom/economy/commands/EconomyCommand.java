package com.coderandom.economy.commands;

import com.coderandom.core.command.BaseCommand;
import com.coderandom.economy.CodeRandomEconomy;
import com.coderandom.economy.VaultEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.logging.Logger;

public final class EconomyCommand extends BaseCommand {
    private static final Economy economy = VaultEconomy.getInstance();
    private static final Logger LOGGER = CodeRandomEconomy.getInstance().getLogger();

    public EconomyCommand() {
        super(
                CodeRandomEconomy.getInstance(),
                "economy",
                new String[]{"econ"},
                "code_random.economy.admin",
                "Administrative economy commands."
        );
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] strings) {

    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
