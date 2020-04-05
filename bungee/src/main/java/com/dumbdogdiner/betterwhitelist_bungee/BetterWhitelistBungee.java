package com.dumbdogdiner.betterwhitelist_bungee;

import com.dumbdogdiner.betterwhitelist_bungee.bungee.commands.UnwhitelistCommand;
import com.dumbdogdiner.betterwhitelist_bungee.bungee.commands.WhitelistCommand;
import com.dumbdogdiner.betterwhitelist_bungee.bungee.commands.WhoisCommand;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.bungee.listeners.PlayerEventListener;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQL;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * The Bungee proxy plugin for propagating whitelist changes/bans to all
 * sub-server instances.
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
        var manager = getProxy().getPluginManager();

        manager.registerListener(this, new PlayerEventListener());
        manager.registerCommand(this, new WhoisCommand());
        manager.registerCommand(this, new WhitelistCommand());
        manager.registerCommand(this, new UnwhitelistCommand());

        WhitelistBot.getInstance().init();
        
        SQL.init();
        SQL.checkTable();
    }

    @Override
    public void onDisable() {
        // Has the unfortunate downside of overriding changes made to the config.
        // Temporarily disabled since config isn't modified anywhere else in the plugin.
        /* PluginConfig.saveConfig(); */

        // Shut down the Discord bot gracefully.
        var jda = WhitelistBot.getJda();
        if (jda != null) {
            WhitelistBot.getJda().shutdown();
        }

        getLogger().info("Aarrff!! (see you again soon :3)");
    }
}