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
import java.util.Optional;

public final class StatusNamePlugin extends JavaPlugin implements TabCompleter {

    // List of allowed status names, loaded from the config.yml
    private List<String> allowedStatuses;

    @Override
    public void onEnable() {
        getConfig().addDefault("allowed-statuses", allowedStatuses);
        getConfig().options().copyDefaults(true);
        saveConfig();

        allowedStatuses = getConfig().getStringList("allowed-statuses");

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

        FileConfiguration config = getConfig();
        allowedStatuses.add(statusName);
        config.set("allowed-statuses", allowedStatuses);
        saveConfig();
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

        FileConfiguration config = getConfig();
        allowedStatuses.remove(statusName);
        config.set("allowed-statuses", allowedStatuses);
        saveConfig();
        sender.sendMessage(ChatColor.GREEN + "The status '" + statusName + ChatColor.GREEN + "' has been removed.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1) {
                List<String> suggestions = new ArrayList<>();

                suggestions.add("clear");
                suggestions.add("set");

                if (sender.hasPermission("createStatus")) {
                    suggestions.add("create");
                    suggestions.add("add");
                }

                if (sender.hasPermission("removeStatus")) {
                    suggestions.add("remove");
                }
                return suggestions;
            } else if (args.length == 2) {
                switch (args[0]) {
                    case "set":
                        return allowedStatuses.stream().map(ChatColor::stripColor).toList();
                    case "add":
                    case "create":
                        if (!sender.hasPermission("createStatus")) return null;
                        return Collections.emptyList();
                    case "remove":
                        if (!sender.hasPermission("removeStatus")) return null;
                        return allowedStatuses.stream().map(ChatColor::stripColor).toList();
                    default:
                        return null;
                }
            }
        }
        return null;
    }
}
