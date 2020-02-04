package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listen for the WS READY event.
 */
public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        // WhitelistBot.getLogger().info("Discord bot has entered the READY state.");
    }
}
