package com.dumbdogdiner.betterwhitelist_client;

import com.dumbdogdiner.betterwhitelist_client.commands.BetterWhitelistCommand;
import com.dumbdogdiner.betterwhitelist_client.listeners.BanListener;

import com.dumbdogdiner.betterwhitelist_client.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterWhitelistClientPlugin extends JavaPlugin {
    public static BungeeMessenger bungee;

    private static BetterWhitelistClientPlugin instance;

    /**
     * Returns a static reference to the plugin.
     * @return
     */
    public static BetterWhitelistClientPlugin getPlugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getCommand("betterwhitelist").setExecutor(new BetterWhitelistCommand());

        // Register event listeners.
        Bukkit.getPluginManager().registerEvents(new BanListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register proxy messenger.
        bungee = new BungeeMessenger(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, bungee.channel);
        getServer().getMessenger().registerIncomingPluginChannel(this, bungee.channel, bungee);

        getLogger().info("Proxy messaging & whitelist initialized.");
        getLogger().info("Will use plugin channel '" + bungee.channel + "' for BungeeCord messaging.");
    }
}
