package de.unvii.statusNamePlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class StatusNamePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("status")) {
            if(sender instanceof Player player) {
                if (args.length == 1) {
                    String status = ChatColor.translateAlternateColorCodes('&', args[0].toUpperCase());

                    String chatName = String.format("[%s§r] %s", status, player.getName());
                    String playerListName = String.format("[%s§r] %s", status, player.getName());

                    player.setDisplayName(chatName);
                    player.setPlayerListName(playerListName);

                    player.sendMessage("§2Der Status '" + status + "§2' wurde gesetzt.");
                    return true;
                } else {
                    // 2 arguments necessary
                    return false;
                }
            } else {
                sender.sendMessage("Only players can execute this command");
                return true;
            }
        }
        return false;
    }
}
