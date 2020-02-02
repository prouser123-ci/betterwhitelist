package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * Listener that waits for members to leave and removes them from the Minecraft whitelist.
 */
public class GuildLeaveListener extends ListenerAdapter {

    public WhitelistBot bot;

    public GuildLeaveListener(WhitelistBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        super.onGuildMemberLeave(event);
    }
}
