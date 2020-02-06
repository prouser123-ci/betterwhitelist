package com.dumbdogdiner.betterwhitelist;

import com.dumbdogdiner.betterwhitelist.commands.BetterWhitelistCommand;
import com.dumbdogdiner.betterwhitelist.listeners.BanListener;

import com.dumbdogdiner.betterwhitelist.utils.SQLConnection;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class BetterWhitelist extends JavaPlugin {
    private static BetterWhitelist instance;

    /**
     * Returns a static reference to the plugin.
     * @return
     */
    public static BetterWhitelist getInstance() {
        return instance;
    }

    public BetterWhitelist() {
        instance = this;
    };

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Register event listeners.
        Bukkit.getPluginManager().registerEvents(new BanListener(), this);

        // Register proxy messenger.
        // bungee = new BungeeMessenger();
        // getServer().getMessenger().registerOutgoingPluginChannel(this, bungee.getChannel());
        // getServer().getMessenger().registerIncomingPluginChannel(this, bungee.getChannel(), bungee);

        // Register commands.
        PluginCommand command = getCommand("betterwhitelist");

        if (command == null) {
            getLogger().warning("Unable to find command 'betterwhitelist'.");
        } else {
            command.setExecutor(new BetterWhitelistCommand());

            // Register completions.
            var commodore = checkCommodoreSupport();
            if (commodore != null) {
                registerCompletions(commodore, command, "btw.commodore");
            }
        }

        SQLConnection.checkTable();

        getLogger().info("Initialization complete >w<");
        // getLogger().info("Will use plugin channel '" + bungee.getChannel() + "' for plugin messaging.");
    }

    /**
     * Register Brigadier auto-completions.
     * @param commodore
     * @param command
     */
    private void registerCompletions(Commodore commodore, PluginCommand command, String resourceName) {
        try {
            var resource = BetterWhitelist.getInstance().getResource(resourceName);

            if (resource == null) {
                throw new IOException("Resource not found.");
            }

            var completions = CommodoreFileFormat.parse(resource);
            commodore.register(command, completions);

        } catch (IOException e) {
            getLogger().warning("Failed to register Brigadier completions for class '" + command.getName() +"'.");
            e.printStackTrace();
        }
    }

    /**
     * Checks if Commodore is supported, and returns the Commodore object if it is.
     * @return
     */
    private Commodore checkCommodoreSupport() {
        if (CommodoreProvider.isSupported()) {
           return CommodoreProvider.getCommodore(this);
        }
        return null;
    }
}
