package de.unvii.statusNamePlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static de.unvii.statusNamePlugin.StatusNamePlugin.selectedStatuses;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        String status = selectedStatuses.get(event.getPlayer().getUniqueId());
        if (status != null) {
            event.getPlayer().sendMessage("Your previous status was: " + status);
        } else {
            event.getPlayer().sendMessage("You had no status before");
        }
    }
}
