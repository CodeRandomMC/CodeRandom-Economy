package com.coderandom.economy.commands;

import com.coderandom.core.UUIDFetcher;
import com.coderandom.core.command.BaseCommand;
import com.coderandom.core.command.CommandUtil;
import com.coderandom.core.utils.MessageUtils;
import com.coderandom.economy.CodeRandomEconomy;
import com.coderandom.economy.EconomyFactory;
import com.coderandom.economy.VaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static com.coderandom.core.command.CommandUtil.*;
import static com.coderandom.economy.EconomyMessages.amountErrorMessage;

public final class EconomyCommand extends BaseCommand {
    private static final VaultEconomy economy = VaultEconomy.getInstance();
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
    public void executeCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            showHelpMenu(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();
        String playerName = args[1];

        new BukkitRunnable() {
            @Override
            public void run() {
                UUID targetUUID = UUIDFetcher.getUUID(playerName);
                if (targetUUID == null) {
                    MessageUtils.formattedErrorMessage(sender, "Player does not exist!");
                    return;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(UUIDFetcher.getUUID(playerName));

                if (targetPlayer == null || !economy.hasAccount(targetPlayer)) {
                    MessageUtils.formattedErrorMessage(sender, "Player's account not found!");
                    return;
                }

                switch (subCommand) {
                    case "balance":
                    case "bal":
                        showPlayerBalance(sender, targetPlayer);
                        break;
                    case "set":
                        setPlayersBalance(sender, targetPlayer, args);
                        break;
                    case "deposit":
                        depositToPlayer(sender, targetPlayer, args);
                        break;
                    case "withdraw":
                        withdrawFromPlayer(sender, targetPlayer, args);
                        break;
                    default:
                        showHelpMenu(sender);
                        break;
                }
            }
        }.runTaskAsynchronously(CodeRandomEconomy.getInstance());
    }

    private void showPlayerBalance(CommandSender sender, OfflinePlayer targetPlayer) {
        String permission = "code_random.economy.admin.balance";
        if (checkPermission(sender, permission)) {
            MessageUtils.messageWithBorder(sender, targetPlayer.getName() + "'s balance: " + economy.format(economy.getBalance(targetPlayer)));
        }
    }

    private void setPlayersBalance(CommandSender sender, OfflinePlayer targetPlayer, String[] args) {
        String permission = "code_random.economy.admin.set";
        if (checkPermission(sender, permission) && checkArgsLength(args, 3)) {
            Double amount = CommandUtil.parseDouble(args[2]);
            if (amount != null && amount >= 0) {
                EconomyFactory.getInstance().setBalance(targetPlayer.getUniqueId(), amount);
                String formatedAmount = economy.format(amount);
                MessageUtils.formattedMessage(sender,"Set " + targetPlayer.getName() + "'s balance to " + formatedAmount);
                LOGGER.info(sender.getName() + " set " + targetPlayer.getName() + "'s balance to " + formatedAmount);
                if (targetPlayer.isOnline()) MessageUtils.formattedMessage(targetPlayer.getPlayer(), "Your balance was set to " + formatedAmount);
            } else {
                amountErrorMessage(sender);
            }
        } else {
            showHelpMenu(sender);
        }
    }

    private void depositToPlayer(CommandSender sender, OfflinePlayer targetPlayer, String[] args) {
        String permission = "code_random.economy.admin.deposit";
        if (checkPermission(sender, permission) && checkArgsLength(args, 3)) {
            double amount = economy.checkPositive(args[2]);
            if (amount > 0.0) {
                economy.depositPlayer(targetPlayer, amount);
                String formatedAmount = economy.format(amount);
                MessageUtils.formattedMessage(sender,"Deposited " + formatedAmount + " in " + targetPlayer.getName() + "'s account.");
                LOGGER.info(sender.getName() + " deposited " + formatedAmount + " in " + targetPlayer.getName() + "'s account.");
                if (targetPlayer.isOnline()) MessageUtils.formattedMessage(targetPlayer.getPlayer(), formatedAmount + " was deposited into your account.");
            } else {
                amountErrorMessage(sender);
            }
        } else {
            showHelpMenu(sender);
        }
    }

    private void withdrawFromPlayer(CommandSender sender, OfflinePlayer targetPlayer, String[] args) {
        String permission = "code_random.economy.admin.withdraw";
        if (checkPermission(sender, permission) && checkArgsLength(args, 3)) {
            double amount = economy.checkPositive(args[2]);
            if (amount > 0.0) {
                economy.withdrawPlayer(targetPlayer, amount);
                String formatedAmount = economy.format(amount);
                MessageUtils.formattedMessage(sender,"Withdrew " + formatedAmount + " from " + targetPlayer.getName() + "'s account.");
                LOGGER.info(sender.getName() + " withdrew " + formatedAmount + " from " + targetPlayer.getName() + "'s account.");
                if (targetPlayer.isOnline()) MessageUtils.formattedMessage(targetPlayer.getPlayer(), formatedAmount + " was withdrawn from your account.");
            } else {
                amountErrorMessage(sender);
            }
        } else {
            showHelpMenu(sender);
        }
    }

    private void showHelpMenu(CommandSender sender) {
        MessageUtils.messageWithTitle(sender,
                "Economy Administration",
                "/economy balance <player> -- Shows a players balance.",
                "/economy set <player> <amount> -- Set a players balance.",
                "/economy deposit <player> <amount> -- Deposit money into a players account.",
                "/economy withdraw <player> <amount> -- Withdraw money from a players account",
                "/economy -- Shows this message."
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandUtil.tabCompleteFilter(args[0], "balance", "set", "withdraw", "deposit");
        } else if (args.length == 2) {
            return CommandUtil.tabCompleteOfflinePlayers(args[1]);
        } else if (args.length == 3)
            return CommandUtil.tabCompleteFilter(args[2], "10", "100", "1000", "<amount>");
        return List.of();
    }
}
