package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static de.unvii.statusNamePlugin.StatusNamePlugin.selectedStatuses;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        String status = selectedStatuses.get(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();

        if (status == null || status.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You had no status before :(");
            player.sendMessage(ChatColor.GREEN + "Try /status set <status> to select a status :)");
        } else {
            player.sendMessage(ChatColor.GREEN + "Getting last status...");
            PlayerStatusManager.setSenderStatus(player, ChatColor.stripColor(status));
        }
    }
}
