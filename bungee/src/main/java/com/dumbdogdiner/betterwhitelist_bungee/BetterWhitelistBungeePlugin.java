package com.dumbdogdiner.betterwhitelist_bungee;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.ConfigManager;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * The Bungee proxy plugin for propagating whitelist changes/bans to all sub-server instances.
 */
public class BetterWhitelistBungeePlugin extends Plugin {
    public InstanceMessenger bungee = new InstanceMessenger(this);
    public SQLConnection sql = new SQLConnection(this);
    public WhitelistBot bot = new WhitelistBot(this);

    public ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);

        getProxy().registerChannel(bungee.channel);
        getProxy().getPluginManager().registerListener(this, bungee);

        bot.init();
        sql.checkTable();

        getLogger().info("Proxy messaging & whitelist initialized.");
        getLogger().info("Will use plugin channel '" + bungee.channel + "' for plugin messaging.");
    }
}