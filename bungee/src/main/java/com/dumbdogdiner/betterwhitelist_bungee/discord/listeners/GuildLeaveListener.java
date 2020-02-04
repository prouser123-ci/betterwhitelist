package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;

/**
 * Listener that waits for members to leave and removes them from the Minecraft whitelist.
 */
public class GuildLeaveListener extends ListenerAdapter {
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent e) {
        var id = e.getMember().getId();

        // Disconnect the player if they are connected.
        var playerUuid = SQLConnection.getUuidFromDiscordId(id);
        if (playerUuid != null) {
            var player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(playerUuid);
            if (player != null) {
                player.disconnect(new TextComponent(ChatColor.RED + "You have left " +
                    e.getGuild().getName() +
                    ", and so have been unwhitelisted from the network!"));
            }
        }

        if (SQLConnection.removeEntry(id)) {
            WhitelistBot.getLogger().info("[discord][leave] Removed player with Discord ID '" + id + "' from the whitelist.");
        }
    }
}
