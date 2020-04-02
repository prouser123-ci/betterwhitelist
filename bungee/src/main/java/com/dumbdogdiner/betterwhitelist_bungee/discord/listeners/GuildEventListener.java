package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQL;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Listener that waits for members to leave/be banned and removes them from the
 * Minecraft whitelist.
 */
public class GuildEventListener extends ListenerAdapter {
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent e) {
        disconnectWithMessage(e.getUser().getId(), ChatColor.RED + "You have left " + e.getGuild().getName()
                + " and have been removed from the whitelist!");
    }

    @Override
    public void onGuildBan(GuildBanEvent e) {
        disconnectWithMessage(e.getUser().getId(), ChatColor.RED + "You were banned from " + e.getGuild().getName()
                + " and have been removed from the whitelist!");
    }

    /**
     * Disconnect a Discord member (if they are connected) with a given message.
     * 
     * @param id
     * @param message
     */
    private static void disconnectWithMessage(String id, String message) {
        if (!PluginConfig.getConfig().getBoolean("discord.enableBanSync")) {
            WhitelistBot.getLogger()
                    .info("[discord] Not removing user '" + id + "' from whitelist - enableBanSync=false");
            return;
        }

        // Disconnect the player if they are connected.
        var playerUuid = SQL.getUuidFromDiscordId(id);
        if (playerUuid == null) {
            return;
        }

        var player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(UUID.fromString(playerUuid));
        if (player == null) {
            return;
        }

        WhitelistBot.getLogger().info("[discord][ban] Disconnecting player if they are still online...");
        player.disconnect(new TextComponent(message));

        if (SQL.removeEntry(id)) {
            WhitelistBot.getLogger()
                    .info("[discord][ban] Removed user with Discord ID '" + id + "' from the whitelist.");
        }
    }
}
