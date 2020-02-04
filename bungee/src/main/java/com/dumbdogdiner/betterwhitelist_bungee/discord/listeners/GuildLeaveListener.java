package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * Listener that waits for members to leave and removes them from the Minecraft whitelist.
 */
public class GuildLeaveListener extends ListenerAdapter {
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent e) {
        var id = e.getMember().getId();

        if (SQLConnection.removeEntry(id)) {
            WhitelistBot.getLogger().info("[discord][leave] Removed player with Discord ID '" + id + "' from the whitelist.");
        }
    }
}
