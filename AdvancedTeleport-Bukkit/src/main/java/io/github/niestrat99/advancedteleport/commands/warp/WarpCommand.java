package io.github.niestrat99.advancedteleport.commands.warp;

import io.github.niestrat99.advancedteleport.api.ATPlayer;
import io.github.niestrat99.advancedteleport.api.Warp;
import io.github.niestrat99.advancedteleport.api.events.ATTeleportEvent;
import io.github.niestrat99.advancedteleport.config.CustomMessages;
import io.github.niestrat99.advancedteleport.config.NewConfig;
import io.github.niestrat99.advancedteleport.managers.CooldownManager;
import io.github.niestrat99.advancedteleport.managers.MovementManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

public class WarpCommand extends AbstractWarpCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (NewConfig.get().USE_WARPS.get()) {
            if (args.length > 0) {
                if (sender.hasPermission("at.member.warp")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        int cooldown = CooldownManager.secondsLeftOnCooldown("warp", player);
                        if (cooldown > 0) {
                            CustomMessages.sendMessage(sender, "Error.onCooldown", "{time}", String.valueOf(cooldown));
                            return true;
                        }
                        if (Warp.getWarps().containsKey(args[0])) {
                            if (MovementManager.getMovement().containsKey(player.getUniqueId())) {
                                CustomMessages.sendMessage(player, "Error.onCountdown");
                                return true;
                            }
                            Warp warp = Warp.getWarps().get(args[0]);
                            warp(warp, player);
                        } else {
                            CustomMessages.sendMessage(sender, "Error.noSuchWarp");
                        }
                    } else {
                        CustomMessages.sendMessage(sender, "Error.notAPlayer");
                    }
                } else {
                    CustomMessages.sendMessage(sender, "Error.noPermission");
                }
            } else {
                CustomMessages.sendMessage(sender, "Error.noWarpInput");
            }
        } else {
            CustomMessages.sendMessage(sender, "Error.featureDisabled");
        }
        return true;
    }

    public static void warp(Warp warp, Player player) {
        boolean found = false;
        if (player.hasPermission("at.member.warp.*")) found = true;
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("at.member.warp." + warp.getName().toLowerCase())) {
                found = permission.getValue();
                break;
            }
        }
        if (!found) {
            CustomMessages.sendMessage(player, "Error.noPermissionWarp", "{warp}", warp.getName());
            return;
        }
        ATTeleportEvent event = new ATTeleportEvent(player, warp.getLocation(), player.getLocation(), warp.getName(), ATTeleportEvent.TeleportType.WARP);
        ATPlayer.getPlayer(player).teleport(event, "warp", "Teleport.teleportingToWarp", NewConfig.get().WARM_UPS.WARP.get());
    }
}
