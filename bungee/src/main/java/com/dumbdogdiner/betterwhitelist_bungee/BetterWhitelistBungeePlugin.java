package com.dumbdogdiner.betterwhitelist_bungee;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * The Bungee proxy plugin for propogating whitelist changes/bans to all sub-server instances.
 */
public class BetterWhitelistBungeePlugin extends Plugin {
    @Override
    public void onEnable() {
        getProxy().registerChannel("BungeeCord");
        getProxy().getPluginManager().registerListener(this, new InstanceMessenger(this));

        getLogger().info("Whitelist & proxy communication enabled.");
    }
}