package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungeePlugin;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * Listener that waits for guild ban events, and removes banned users from the Minecraft whitelist.
 */
public class GuildBanListener extends ListenerAdapter {

    public WhitelistBot bot;

    public GuildBanListener(WhitelistBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        super.onGuildBan(event);
    }
}
