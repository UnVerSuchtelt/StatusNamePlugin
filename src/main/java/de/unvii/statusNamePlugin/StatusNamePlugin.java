package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class StatusNamePlugin extends JavaPlugin {

    // List of allowed status names, loaded from the config.yml
    private List<String> allowedStatuses;

    @Override
    public void onEnable() {
        // Load configuration
        // If the config doesn't exist yet, it will be created with default values
        getConfig().addDefault("allowed-statuses", allowedStatuses);
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load the list of allowed status names from the config.yml
        allowedStatuses = getConfig().getStringList("allowed-statuses");

        getLogger().info("StatusNamePlugin activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StatusNamePlugin deactivated!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the "/status" command is used
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1) {
                String statusName = args[0];

                // Command "/status clear" (Delete status)
                if (statusName.equalsIgnoreCase("clear")) {
                    if (sender instanceof Player player) {
                        clearPlayerStatus(player);  // Delete the player's status
                        player.sendMessage(ChatColor.GREEN + "Your status has been cleared.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    }
                    return true;
                }

                // Set the player's status
                if (allowedStatuses.contains(statusName)) {
                    if (sender instanceof Player player) {
                        setPlayerStatus(player, statusName);  // Set the status
                        player.sendMessage(ChatColor.GREEN + "Your status has been set to '" + statusName + "'.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid status! Please choose a status from: " + String.join(", ", allowedStatuses));
                }
            }

            // /status create <statusName>
            else if (args.length == 2 && args[0].equalsIgnoreCase("create") && sender.hasPermission("createStatus")) {
                String newStatus = args[1];

                // Check if the status already exists
                if (allowedStatuses.contains(newStatus)) {
                    sender.sendMessage(ChatColor.RED + "This status already exists.");
                    return false;
                }

                // Add the new status to config.yml
                addStatus(newStatus);
                sender.sendMessage(ChatColor.GREEN + "The status '" + newStatus + "' has been created and added to the config.");
                return true;
            }

            // /status remove <statusName>
            else if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("removeStatus")) {
                String statusToRemove = args[1];

                // Check if the status exists
                if (!allowedStatuses.contains(statusToRemove)) {
                    sender.sendMessage(ChatColor.RED + "This status does not exist.");
                    return false;
                }

                // Remove the status from config.yml
                removeStatus(statusToRemove);
                sender.sendMessage(ChatColor.GREEN + "The status '" + statusToRemove + "' has been removed.");
                return true;
            }
        }
        return false;
    }

    private void setPlayerStatus(Player player, String statusName) {
        String newPlayerName = ChatColor.GREEN + "[" + statusName + "] " + player.getName();

        player.setPlayerListName(newPlayerName);
        player.setDisplayName(newPlayerName);
    }

    private void clearPlayerStatus(Player player) {
        // Remove the status from the player list (set to the normal player name)
        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
    }

    private void addStatus(String newStatus) {
        // Load the current configuration
        FileConfiguration config = getConfig();

        // Add the new status to the list
        allowedStatuses.add(newStatus);

        // Save the updated list to the config
        config.set("allowed-statuses", allowedStatuses);
        saveConfig();  // Save the configuration
    }

    private void removeStatus(String statusToRemove) {
        // Load the current configuration
        FileConfiguration config = getConfig();

        // Remove the status from the list
        allowedStatuses.remove(statusToRemove);

        // Save the updated list to the config
        config.set("allowed-statuses", allowedStatuses);
        saveConfig();  // Save the configuration
    }
}
