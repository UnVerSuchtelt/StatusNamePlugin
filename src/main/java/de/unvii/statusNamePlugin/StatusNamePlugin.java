package de.unvii.statusNamePlugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class StatusNamePlugin extends JavaPlugin {

    public static JavaPlugin plugin;

    public static List<String> allowedStatuses;

    @Override
    public void onEnable() {
        plugin = this;

        createDefaultConfig();
        allowedStatuses = getConfig().getStringList("allowed-statuses");

        PluginCommand statusCommand = getCommand("status");
        if (statusCommand != null) {
            statusCommand.setExecutor(new StatusCommand());
            statusCommand.setTabCompleter(new StatusCommandCompleter());
        }

        getLogger().info("StatusNamePlugin activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StatusNamePlugin deactivated!");
    }

    private void createDefaultConfig() {
        getConfig().addDefault("allowed-statuses", allowedStatuses);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
