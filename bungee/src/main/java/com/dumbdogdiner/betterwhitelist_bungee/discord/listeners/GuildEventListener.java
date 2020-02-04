package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;

/**
 * Listener that waits for members to leave/be banned and removes them from the Minecraft whitelist.
 */
public class GuildEventListener extends ListenerAdapter {
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

    @Override
    public void onGuildBan(GuildBanEvent e) {
        var id = e.getUser().getId();

        if (!PluginConfig.getConfig().getBoolean("enableBanSync")) {
            WhitelistBot.getLogger().info("[discord] Not removing user '" + id + "' from whitelist - enableBanSync=false");
            return;
        }

        // Disconnect the player if they are connected.
        var playerUuid = SQLConnection.getUuidFromDiscordId(id);
        if (playerUuid != null) {
            var player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(playerUuid);
            if (player != null) {
                player.disconnect(new TextComponent(ChatColor.RED + "You have been banned from " +
                        e.getGuild().getName() +
                        ", and so have been unwhitelisted from the network!"));
            }
        }

        if (SQLConnection.removeEntry(id)) {
            WhitelistBot.getLogger().info("[discord][ban] Removed user with Discord ID '" + id + "' from the whitelist.");
        }
    }
}