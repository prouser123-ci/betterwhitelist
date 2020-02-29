package com.dumbdogdiner.betterwhitelist_bungee;

import com.dumbdogdiner.betterwhitelist_bungee.bungee.commands.WhoisCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.bungee.listeners.PlayerEventListener;
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
        getProxy().getPluginManager().registerListener(this, new PlayerEventListener());
        getProxy().getPluginManager().registerCommand(this, new WhoisCommand());

        WhitelistBot.getInstance().init();

        SQLConnection.checkTable();
    }

    @Override
    public void onDisable() {
        PluginConfig.saveConfig();
        getLogger().info("Aarrff!! (see you again soon :3)");
    }
}