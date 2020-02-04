package com.dumbdogdiner.betterwhitelist_bungee;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * The Bungee proxy plugin for propagating whitelist changes/bans to all sub-server instances.
 */
public class BetterWhitelistBungee extends Plugin {

    private static BetterWhitelistBungee instance;
    public static BetterWhitelistBungee getInstance() {
        return instance;
    }

    // Spigot only calls the constructor method once, so this should work.
    public BetterWhitelistBungee() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getProxy().registerChannel(InstanceMessenger.getChannel());
        getProxy().getPluginManager().registerListener(this, InstanceMessenger.getInstance());

        WhitelistBot.getInstance().start();
        getLogger().info("[discord] Spawned WhitelistBot thread.");

        SQLConnection.checkTable();

        getLogger().info("Will use channel '" + InstanceMessenger.getChannel() + "' for plugin messaging.");
    }

    @Override
    public void onDisable() {
        PluginConfig.saveConfig();
        getProxy().unregisterChannel(InstanceMessenger.getChannel());
    }
}