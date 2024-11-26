package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatusCommandCompleter implements TabCompleter {

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
                        return StatusNamePlugin.allowedStatuses.stream().map(ChatColor::stripColor).toList();
                    case "add":
                    case "create":
                        if (!sender.hasPermission("createStatus")) return null;
                        return Collections.emptyList();
                    case "remove":
                        if (!sender.hasPermission("removeStatus")) return null;
                        return StatusNamePlugin.allowedStatuses.stream().map(ChatColor::stripColor).toList();
                    default:
                        return null;
                }
            }
        }
        return null;
    }
}
