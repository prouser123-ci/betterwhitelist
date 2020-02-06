package com.dumbdogdiner.betterwhitelist.listeners;

import com.dumbdogdiner.betterwhitelist.BetterWhitelist;
import com.dumbdogdiner.betterwhitelist.utils.SQLConnection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener that syncs local server bans to all other servers.
 */
public class BanListener implements Listener {
    public boolean banSyncEnabled;

    public BanListener() {
        banSyncEnabled = BetterWhitelist.getInstance().getConfig().getBoolean("enableBanSync");
    }

    @EventHandler
    public void onPlayerBan(PlayerQuitEvent e) {

        Player target = e.getPlayer();

        if (!banSyncEnabled) {
            BetterWhitelist.getInstance().getLogger().info(String.format("Not checking if '%u' should be banned - banSyncEnabled=false", target.getUniqueId().toString()));
            return;
        }

        if (!target.isBanned()) {
            return;
        }

        var uuid = e.getPlayer().getUniqueId().toString();
        if (SQLConnection.removeEntryUsingUuid(uuid)) {
            BetterWhitelist.getInstance().getLogger().info("Removed user with Discord ID '" + uuid + "' from the whitelist.");
        }
    }
}
