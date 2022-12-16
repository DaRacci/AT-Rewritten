package io.github.niestrat99.advancedteleport.commands;

import io.github.niestrat99.advancedteleport.config.CustomMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the base implementation for a command in AT.
 */
public abstract class ATCommand implements TabExecutor {

    /**
     * Determines whether the command can continue after performing basic checks.<br>
     * This will check if the feature for the command is enabled, and if the sender<br>
     * has permission.
     *
     * @param sender the command sender running the command.
     * @return true if the sender can run the command, false if not.
     */
    @Contract(pure = true)
    protected boolean canProceed(@NotNull final CommandSender sender) {
        // Make sure the required feature is enabled
        if (!getRequiredFeature()) {
            CustomMessages.sendMessage(sender, "Error.featureDisabled");
            return false;
        }

        // If it's enabled, check for permission
        return sender.hasPermission(getPermission());
    }

    @Contract(pure = true)
    public abstract boolean getRequiredFeature();

    @Contract(pure = true)
    protected abstract @NotNull String getPermission();

    public Void handleCommandFeedback(Throwable ex, CommandSender sender, String success, String failure, String... placeholders) {
        // If an error occurred, send the error and print the stacktrace
        if (ex != null) {
            CustomMessages.sendMessage(sender, failure, placeholders);
            ex.printStackTrace();
            return null;
        }

        // Otherwise, just send the success message
        CustomMessages.sendMessage(sender, success, placeholders);
        return null;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final var checkVisibility = sender instanceof Player;
        final var players = Bukkit.getOnlinePlayers().stream()
            .filter(player -> !checkVisibility || ((Player) sender).canSee(player))
            .map(Player::getName).toList();

        return StringUtil.copyPartialMatches(args[args.length - 1], players, new ArrayList<>());
    }
}
