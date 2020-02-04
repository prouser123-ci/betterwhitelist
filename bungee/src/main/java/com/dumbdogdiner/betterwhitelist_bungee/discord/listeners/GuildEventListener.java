package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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
        disconnectWithMessage(
            e.getGuild(),
            e.getUser().getId(),
            ChatColor.RED + "You have left " + e.getGuild().getName() + " and have been removed from the whitelist!"
        );
    }

    @Override
    public void onGuildBan(GuildBanEvent e) {
        disconnectWithMessage(
                e.getGuild(),
                e.getUser().getId(),
                ChatColor.RED + "You were banned from " + e.getGuild().getName() + " and have been removed from the whitelist!"
        );
    }

    /**
     * Disconnect a Discord member (if they are connected) with a given message.
     * @param guild
     * @param id
     * @param message
     */
    private static void disconnectWithMessage(Guild guild, String id, String message) {
        if (!PluginConfig.getConfig().getBoolean("enableBanSync")) {
            WhitelistBot.getLogger().info("[discord] Not removing user '" + id + "' from whitelist - enableBanSync=false");
            return;
        }

        // Disconnect the player if they are connected.
        var playerUuid = SQLConnection.getUuidFromDiscordId(id);
        if (playerUuid != null) {
            var player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(playerUuid);
            if (player != null) {
                player.disconnect(new TextComponent(message));
            }
        }

        if (SQLConnection.removeEntry(id)) {
            WhitelistBot.getLogger().info("[discord][ban] Removed user with Discord ID '" + id + "' from the whitelist.");
        }
    }
}
