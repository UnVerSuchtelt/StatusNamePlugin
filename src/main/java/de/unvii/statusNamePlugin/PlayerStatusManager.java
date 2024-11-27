package de.unvii.statusNamePlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static de.unvii.statusNamePlugin.StatusNamePlugin.*;

public class PlayerStatusManager {
    public static void clearSenderStatus(CommandSender sender) {
        if (sender instanceof Player player) {
            player.setPlayerListName(player.getName());
            player.setDisplayName(player.getName());

            selectedStatuses.put(player.getUniqueId(), "");
            plugin.getConfig().set("selected-statuses", ConverterHelper.convertMapToSeparatedList(selectedStatuses));
            plugin.saveConfig();

            player.sendMessage(ChatColor.GREEN + "Your status has been cleared.");
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }
    }

    public static void setSenderStatus(CommandSender sender, String statusNameWithoutAnyColor) {
        Optional<String> potentialStatusName = allowedStatuses.stream() //
                .filter(s -> ChatColor.stripColor(s).equalsIgnoreCase(statusNameWithoutAnyColor))
                .findFirst();

        if (potentialStatusName.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "'" + statusNameWithoutAnyColor + "' is an invalid status! Please choose a status from: " + String.join(ChatColor.RED + ", ", allowedStatuses));
            return;
        }

        String statusName = potentialStatusName.get();

        if (sender instanceof Player player) {
            String newPlayerName = ChatColor.RESET + "[" + statusName + ChatColor.RESET + "] " + player.getName();
            player.setPlayerListName(newPlayerName);
            player.setDisplayName(newPlayerName);

            selectedStatuses.put(player.getUniqueId(), statusName);
            plugin.getConfig().set("selected-statuses", ConverterHelper.convertMapToSeparatedList(selectedStatuses));
            plugin.saveConfig();

            player.sendMessage(ChatColor.GREEN + "Your status has been set to '" + statusName + ChatColor.GREEN + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }
    }

    public static void createStatus(CommandSender sender, String statusNameWithWrongColorCode) {
        String statusName = ChatColor.translateAlternateColorCodes('&', statusNameWithWrongColorCode);
        if (allowedStatuses.contains(statusName)) {
            sender.sendMessage(ChatColor.RED + "This status already exists.");
            return;
        }

        allowedStatuses.add(statusName);
        plugin.getConfig().set("allowed-statuses", allowedStatuses);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "The status '" + statusName + ChatColor.GREEN + "' has been created and added to the config.");
    }

    public static void removeStatus(CommandSender sender, String statusNameWithoutAnyColor) {
        Optional<String> potentialStatusName = allowedStatuses.stream() //
                .filter(s -> ChatColor.stripColor(s).equalsIgnoreCase(statusNameWithoutAnyColor))
                .findFirst();

        if (potentialStatusName.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "This status does not exist.");
            return;
        }

        String statusName = potentialStatusName.get();

        if (selectedStatuses.containsValue(statusName)) {
            List<UUID> uuidsToRemove = selectedStatuses.entrySet().stream() //
                    .filter(entry -> entry.getValue().equalsIgnoreCase(statusName)) //
                    .map(Map.Entry::getKey) //
                    .toList();

            // clear status from all online players
            uuidsToRemove.stream() //
                    .map(Bukkit::getPlayer) //
                    .filter(Bukkit.getOnlinePlayers()::contains) //
                    .forEach(PlayerStatusManager::clearSenderStatus);

            // remove last statuses in config file (necessary because of offline players)
            uuidsToRemove.forEach(selectedStatuses::remove);

            plugin.getConfig().set("selected-statuses", ConverterHelper.convertMapToSeparatedList(selectedStatuses));
            plugin.saveConfig();
        }

        allowedStatuses.remove(statusName);
        plugin.getConfig().set("allowed-statuses", allowedStatuses);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "The status '" + statusName + ChatColor.GREEN + "' has been removed.");
    }
}
