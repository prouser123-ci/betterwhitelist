package com.dumbdogdiner.betterwhitelist_client;

import com.dumbdogdiner.betterwhitelist_client.commands.BetterWhitelistCommand;
import com.dumbdogdiner.betterwhitelist_client.listeners.BanListener;

import com.dumbdogdiner.betterwhitelist_client.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterWhitelistClientPlugin extends JavaPlugin {
    public static BungeeMessenger bungee = new BungeeMessenger();

    /**
     * Returns a static reference to the plugin.
     * @return
     */
    public static Plugin getPlugin() {
        return getPlugin(BetterWhitelistClientPlugin.class);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("betterwhitelist").setExecutor(new BetterWhitelistCommand());

        // Register event listeners.
        Bukkit.getPluginManager().registerEvents(new BanListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        // Register proxy messenger.
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungee);

        Bukkit.getLogger().info("Whitelist initialized.");
    }
}
