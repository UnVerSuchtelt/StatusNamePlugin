package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StatusNamePlugin extends JavaPlugin implements TabCompleter {

    // List of allowed status names, loaded from the config.yml
    private List<String> allowedStatuses;

    @Override
    public void onEnable() {
        // Load configuration
        getConfig().addDefault("allowed-statuses", allowedStatuses);
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load the list of allowed status names from the config.yml
        allowedStatuses = getConfig().getStringList("allowed-statuses");

        // Register the tab completer
        getCommand("status").setTabCompleter(this);

        getLogger().info("StatusNamePlugin activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StatusNamePlugin deactivated!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1) {
                String statusName = args[0];

                // Command "/status clear" (Delete status)
                if (statusName.equalsIgnoreCase("clear")) {
                    if (sender instanceof Player player) {
                        clearPlayerStatus(player);
                        player.sendMessage(ChatColor.GREEN + "Your status has been cleared.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    }
                    return true;
                }

                // Set the player's status
                if (allowedStatuses.contains(statusName)) {
                    if (sender instanceof Player player) {
                        setPlayerStatus(player, statusName);
                        player.sendMessage(ChatColor.GREEN + "Your status has been set to '" + statusName + "'.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid status! Please choose a status from: " + String.join(", ", allowedStatuses));
                }
            } else if (args.length == 2 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add")) && sender.hasPermission("createStatus")) {
                String newStatus = args[1];

                if (allowedStatuses.contains(newStatus)) {
                    sender.sendMessage(ChatColor.RED + "This status already exists.");
                    return false;
                }

                addStatus(newStatus);
                sender.sendMessage(ChatColor.GREEN + "The status '" + newStatus + "' has been created and added to the config.");
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("removeStatus")) {
                String statusToRemove = args[1];

                if (!allowedStatuses.contains(statusToRemove)) {
                    sender.sendMessage(ChatColor.RED + "This status does not exist.");
                    return false;
                }

                removeStatus(statusToRemove);
                sender.sendMessage(ChatColor.GREEN + "The status '" + statusToRemove + "' has been removed.");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1) {
                List<String> suggestions = new ArrayList<>();

                if (sender.hasPermission("createStatus")) {
                    suggestions.add("create");
                    suggestions.add("add");
                }
                if (sender.hasPermission("removeStatus")) {
                    suggestions.add("remove");
                }

                suggestions.add("clear");
                suggestions.addAll(allowedStatuses);

                return suggestions;
            } else if (args.length == 2) {
                if ((args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add")) && sender.hasPermission("createStatus")) {
                    // Suggest nothing specific for /status create or /status add
                    return Collections.emptyList();
                } else if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("removeStatus")) {
                    // Suggest allowed statuses for /status remove
                    return allowedStatuses;
                }
            }
        }
        return null; // Return null to allow default behavior
    }

    private void setPlayerStatus(Player player, String statusName) {
        String newPlayerName = "[" + statusName + "] " + player.getName();

        player.setPlayerListName(newPlayerName);
        player.setDisplayName(newPlayerName);
    }

    private void clearPlayerStatus(Player player) {
        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
    }

    private void addStatus(String newStatus) {
        FileConfiguration config = getConfig();
        allowedStatuses.add(newStatus);
        config.set("allowed-statuses", allowedStatuses);
        saveConfig();
    }

    private void removeStatus(String statusToRemove) {
        FileConfiguration config = getConfig();
        allowedStatuses.remove(statusToRemove);
        config.set("allowed-statuses", allowedStatuses);
        saveConfig();
    }
}
