package com.dumbdogdiner.betterwhitelist_client.listeners;

import com.dumbdogdiner.betterwhitelist_client.BetterWhitelistClientPlugin;
import com.dumbdogdiner.betterwhitelist_client.BungeeMessenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener that syncs local server bans to all other servers.
 */
public class BanListener implements Listener {
    private boolean banSyncEnabled = BetterWhitelistClientPlugin.getPlugin().getConfig().getBoolean("enableBanSync");
    private BungeeMessenger bungee = BetterWhitelistClientPlugin.bungee;

    @EventHandler
    public void onPlayerBan(PlayerQuitEvent e) {
        if (!banSyncEnabled) {
            return;
        }

        Player target = e.getPlayer();

        if (target.isBanned()) {
            return;
        }

        // Attempt to tell Bungee - need to have an online player to send messages.
        Player receiver;
        if (target.isOnline()) {
            receiver = target;
        } else {
            receiver = Bukkit.getOnlinePlayers().iterator().next();
        }

        if (!receiver.isOnline()) {
            Bukkit.getLogger().info("No players are online - cannot inform Bungee of ban. Caching until somebody joins...");
            // TODO: Add cache.
            return;
        }


        // TODO: Check if player is banned with BungeeCord.
        bungee.addGlobalBan(receiver, target.getUniqueId());
    }
}
