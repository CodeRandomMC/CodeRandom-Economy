package com.coderandom.economy.commands;

import com.coderandom.core.UUIDFetcher;
import com.coderandom.core.command.BaseCommand;
import com.coderandom.core.command.CommandUtil;
import com.coderandom.core.utils.MessageUtils;
import com.coderandom.economy.CodeRandomEconomy;
import com.coderandom.economy.VaultEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class PayCommand extends BaseCommand {
    private static final Economy economy = VaultEconomy.getInstance();
    private static final Logger LOGGER = CodeRandomEconomy.getInstance().getLogger();

    public PayCommand() {
        super(
                CodeRandomEconomy.getInstance(),
                "pay",
                null,
                "code_random.economy.user.pay",
                "Allows a user to send a player money."
        );
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2) {
                UUID targetUUID = UUIDFetcher.getUUID(args[0]);
                if (targetUUID == null) {
                    MessageUtils.formattedErrorMessage(sender, "Player " + args[0] + "does not exist!");
                    return;
                }

                if (Bukkit.getOfflinePlayer(targetUUID) instanceof OfflinePlayer targetPlayer) {
                    double amount;
                    if (!economy.hasAccount(targetPlayer)) {
                        MessageUtils.formattedErrorMessage(player,"Player account not found!");
                        sendUsage(sender);
                        return;
                    }

                    if (Objects.equals(player.getName(), targetPlayer.getName())) {
                        MessageUtils.formattedErrorMessage(player, "You can't send money to yourself!");
                        return;
                    }

                    try {
                        amount = Double.parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        MessageUtils.formattedErrorMessage(sender, "Amount must be a number.");
                        sendUsage(sender);
                        return;
                    }

                    if (amount < 0) {
                        MessageUtils.formattedErrorMessage(sender, "Amount has to be more that 0.");
                        sendUsage(sender);
                        return;
                    }

                    if (economy.has(player, amount)) {
                        economy.withdrawPlayer(player, amount);
                        economy.depositPlayer(targetPlayer, amount);
                        MessageUtils.formattedMessage(player, "Paid " + economy.format(amount) + " to player: " + args[0]);
                        if (targetPlayer.isOnline()) MessageUtils.formattedMessage(targetPlayer.getPlayer(), "Received " + economy.format(amount) + " from " + player.getName());
                        LOGGER.log(Level.INFO, player.getName() + " paid " + economy.format(amount) + " to " + targetPlayer.getName());
                    } else {
                        MessageUtils.formattedErrorMessage(sender, "You don't have enough to complete this transaction!");
                    }
                } else {
                    MessageUtils.formattedErrorMessage(sender, "Player not found!");
                }

            } else {
                sendUsage(sender);
            }

        } else {
            MessageUtils.formattedErrorMessage(sender,"Must be a player to send this command!");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandUtil.tabCompleteOnlinePlayers(args[0]);
        } else if (args.length == 2) {
            return CommandUtil.tabCompleteFilter(args[1], "10", "100", "1000", "<amount>");
        }
        return List.of();
    }

    private void sendUsage(CommandSender sender) {
        MessageUtils.formattedMessage(sender, "/pay <player> <amount>");
    }
}
