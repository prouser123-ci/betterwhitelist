package com.dumbdogdiner.betterwhitelist_bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BetterWhitelistBungeePlugin extends Plugin {
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
    }
}