package com.dumbdogdiner.betterwhitelist_client.listeners;

import com.dumbdogdiner.betterwhitelist_client.BetterWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener that syncs local server bans to all other servers.
 */
public class BanListener implements Listener {
    public BetterWhitelist plugin;
    public boolean banSyncEnabled;

    public BanListener(BetterWhitelist plugin) {
        this.plugin = plugin;
        banSyncEnabled = plugin.getConfig().getBoolean("enableBanSync");
    }

    @EventHandler
    public void onPlayerBan(PlayerQuitEvent e) {


        Player target = e.getPlayer();

        if (!banSyncEnabled) {
            plugin.getLogger().info(String.format("Not checking if '%u' should be banned - banSyncEnabled=false", target.getUniqueId().toString()));
            return;
        }

        if (!target.isBanned()) {
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
            plugin.getLogger().info("No players are online - cannot inform Bungee of ban. Caching until somebody joins...");
            // TODO: Add cache.
            return;
        }


        // TODO: Check if player is banned with BungeeCord.
        plugin.bungee.addGlobalBan(receiver, target.getUniqueId());
    }
}
