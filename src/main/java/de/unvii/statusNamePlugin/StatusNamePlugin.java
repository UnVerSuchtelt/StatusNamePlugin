package de.unvii.statusNamePlugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class StatusNamePlugin extends JavaPlugin {

    public static JavaPlugin plugin;

    public static List<String> allowedStatuses;
    public static Map<UUID, String> selectedStatuses;

    @Override
    public void onEnable() {
        plugin = this;

        createDefaultConfig();
        allowedStatuses = getConfig().getStringList("allowed-statuses");
        selectedStatuses = ConverterHelper.convertSeparatedListToMap(getConfig().getStringList("selected-statuses"));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

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
        getConfig().addDefault("selected-statuses", selectedStatuses);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
