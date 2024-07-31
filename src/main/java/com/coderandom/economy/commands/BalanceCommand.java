package com.coderandom.economy.commands;

import com.coderandom.core.command.BaseCommand;
import com.coderandom.economy.CodeRandomEconomy;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BalanceCommand extends BaseCommand {
    protected BalanceCommand() {
        super(
                CodeRandomEconomy.getInstance(),
                "balance",
                new String[] { "bal" },
                "code_random.economy.balance",
                "/balance",
                "Allows a user to see there balance"
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
