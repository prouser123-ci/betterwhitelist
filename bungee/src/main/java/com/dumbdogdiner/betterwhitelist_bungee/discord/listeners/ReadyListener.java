package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungeePlugin;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listen for the WS READY event.
 */
public class ReadyListener extends ListenerAdapter {
    public WhitelistBot bot;

    public ReadyListener(WhitelistBot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        bot.logger.info("Discord bot has entered the READY state.");
    }
}
