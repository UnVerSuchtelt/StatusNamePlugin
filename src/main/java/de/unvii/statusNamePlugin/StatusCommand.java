package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
                    PlayerStatusManager.clearSenderStatus(sender);
                    break;
                case "set":
                    if (args.length < 2) return false;
                    PlayerStatusManager.setSenderStatus(sender, args[1]);
                    break;
                case "add":
                case "create":
                    if (args.length < 2) return false;
                    if (!sender.hasPermission("createStatus")) return true;
                    PlayerStatusManager.createStatus(sender, args[1]);
                    break;
                case "remove":
                    if (args.length < 2) return false;
                    if (!sender.hasPermission("removeStatus")) return true;
                    PlayerStatusManager.removeStatus(sender, args[1]);
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "'" + operation + "' is not a valid operation.");
                    break;
            }
            return true;
        }
        return false;
    }

}
