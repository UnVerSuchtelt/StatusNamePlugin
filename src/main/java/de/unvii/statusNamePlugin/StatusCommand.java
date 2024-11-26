package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static de.unvii.statusNamePlugin.StatusNamePlugin.*;

public class StatusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length < 1) {
                return false;
            }

            String operation = args[0];
            switch (operation) {
                case "clear":
                    clearSenderStatus(sender);
                    break;
                case "set":
                    if (args.length < 2) return false;
                    setSenderStatus(sender, args[1]);
                    break;
                case "add":
                case "create":
                    if (args.length < 2) return false;
                    if (!sender.hasPermission("createStatus")) return true;
                    createStatus(sender, args[1]);
                    break;
                case "remove":
                    if (args.length < 2) return false;
                    if (!sender.hasPermission("removeStatus")) return true;
                    removeStatus(sender, args[1]);
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "'" + operation + "' is not a valid operation.");
                    break;
            }
            return true;
        }
        return false;
    }

    private void clearSenderStatus(CommandSender sender) {
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

    private void setSenderStatus(CommandSender sender, String statusNameWithoutAnyColor) {
        Optional<String> potentialStatusName = allowedStatuses.stream() //
                .filter(s -> ChatColor.stripColor(s).equalsIgnoreCase(statusNameWithoutAnyColor))
                .findFirst();

        if (potentialStatusName.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Invalid status! Please choose a status from: " + String.join(ChatColor.RED + ", ", allowedStatuses));
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

    private void createStatus(CommandSender sender, String statusNameWithWrongColorCode) {
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

    private void removeStatus(CommandSender sender, String statusNameWithoutAnyColor) {
        Optional<String> potentialStatusName = allowedStatuses.stream() //
                .filter(s -> ChatColor.stripColor(s).equalsIgnoreCase(statusNameWithoutAnyColor))
                .findFirst();

        if (potentialStatusName.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "This status does not exist.");
            return;
        }

        String statusName = potentialStatusName.get();

        allowedStatuses.remove(statusName);
        plugin.getConfig().set("allowed-statuses", allowedStatuses);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "The status '" + statusName + ChatColor.GREEN + "' has been removed.");
    }

}
