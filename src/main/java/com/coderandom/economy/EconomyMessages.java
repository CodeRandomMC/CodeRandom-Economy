package com.coderandom.economy;

import com.coderandom.core.utils.MessageUtils;
import org.bukkit.command.CommandSender;

public final class EconomyMessages {
    public static void amountErrorMessage(CommandSender sender) {
        MessageUtils.formattedErrorMessage(sender, "Amount not valid!");
    }
}
