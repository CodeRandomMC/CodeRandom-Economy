package com.coderandom.economy.commands;

import com.coderandom.core.command.BaseCommand;
import com.coderandom.core.utils.MessageUtils;
import com.coderandom.economy.CodeRandomEconomy;
import com.coderandom.economy.VaultEconomy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BalanceCommand extends BaseCommand {
    public BalanceCommand() {
        super(
                CodeRandomEconomy.getInstance(),
                "balance",
                new String[] { "bal" },
                "code_random.economy.user.balance",
                "Allows a user to see there balance"
        );
    }

    @Override
    public void executeCommand(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player player) {
            double balance = VaultEconomy.getInstance().getBalance(player);
            String message = "Balance: " + VaultEconomy.getInstance().format(balance);
            MessageUtils.messageWithBorder(player, message);
        } else {
            MessageUtils.formattedMessage(commandSender,"Must be a player to send this command!");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
