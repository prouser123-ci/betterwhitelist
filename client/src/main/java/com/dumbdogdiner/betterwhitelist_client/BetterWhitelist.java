package com.dumbdogdiner.betterwhitelist_client;

import com.dumbdogdiner.betterwhitelist_client.commands.BetterWhitelistCommand;
import com.dumbdogdiner.betterwhitelist_client.listeners.BanListener;

import com.dumbdogdiner.betterwhitelist_client.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BetterWhitelist extends JavaPlugin {
    public static BungeeMessenger bungee;

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
        try {
            getCommand("betterwhitelist").setExecutor(new BetterWhitelistCommand());
        } catch (NullPointerException e) {
            e.printStackTrace();
            instance.getLogger().log(Level.SEVERE, "Commmand \"betterwhitelist\" not found!");
        }
        // Register event listeners.
        Bukkit.getPluginManager().registerEvents(new BanListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register proxy messenger.
        bungee = new BungeeMessenger();
        getServer().getMessenger().registerOutgoingPluginChannel(this, bungee.getChannel());
        getServer().getMessenger().registerIncomingPluginChannel(this, bungee.getChannel(), bungee);

        getLogger().info("Proxy messaging & whitelist initialized.");
        getLogger().info("Will use plugin channel '" + bungee.getChannel() + "' for plugin messaging.");
    }
}
