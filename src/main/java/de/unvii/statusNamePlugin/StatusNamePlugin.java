package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            if (sender instanceof Player player) {
                clearPlayerStatus(player);
                player.sendMessage(ChatColor.GREEN + "Your status has been cleared.");
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            }
            return true;
        }

        if (args.length > 1) {
            String operation = args[0];
            String statusName = args[1];

            switch (operation) {
                case "set":
                    if (!allowedStatuses.contains(statusName)) {
                        sender.sendMessage(ChatColor.RED + "Invalid status! Please choose a status from: " + String.join(", ", allowedStatuses));
                        break;
                    }

                    if (sender instanceof Player player) {
                        setPlayerStatus(player, statusName);
                        player.sendMessage(ChatColor.GREEN + "Your status has been set to '" + statusName + "'.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    }
                    break;
                case "add":
                case "create":
                    if (sender.hasPermission("createStatus")) {
                        if (allowedStatuses.contains(statusName)) {
                            sender.sendMessage(ChatColor.RED + "This status already exists.");
                            break;
                        }

                        addStatus(statusName);
                        sender.sendMessage(ChatColor.GREEN + "The status '" + statusName + "' has been created and added to the config.");
                    }
                    break;
                case "remove":
                    if (sender.hasPermission("removeStatus")) {
                        if (!allowedStatuses.contains(statusName)) {
                            sender.sendMessage(ChatColor.RED + "This status does not exist.");
                            break;
                        }

                        removeStatus(statusName);
                        sender.sendMessage(ChatColor.GREEN + "The status '" + statusName + "' has been removed.");
                    }
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "'" + operation + "' is not a valid operation.");
                    break;
            }
            return true;
        }
        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a valid operation.");
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
                suggestions.add("set");

                return suggestions;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    return allowedStatuses;
                } else if ((args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add")) && sender.hasPermission("createStatus")) {
                    return Collections.emptyList();
                } else if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("removeStatus")) {
                    return allowedStatuses;
                }
            }
        }
        return null;
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
