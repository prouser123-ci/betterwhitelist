package com.dumbdogdiner.betterwhitelist_bungee;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * The Bungee proxy plugin for propagating whitelist changes/bans to all sub-server instances.
 */
public class BetterWhitelistBungeePlugin extends Plugin {
    public InstanceMessenger bungee = new InstanceMessenger(this);

    @Override
    public void onEnable() {
        getProxy().registerChannel(bungee.channel);
        getProxy().getPluginManager().registerListener(this, bungee);

        getLogger().info("Proxy messaging & whitelist initialized.");
        getLogger().info("Will use plugin channel '" + bungee.channel + "' for BungeeCord messaging.");
    }
}