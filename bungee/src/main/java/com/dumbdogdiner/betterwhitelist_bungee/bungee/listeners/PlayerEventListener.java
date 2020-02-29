package com.dumbdogdiner.betterwhitelist_bungee.bungee.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.utils.MojangUser;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Listen for player joins and check them against the SQL database.
 */
public class PlayerEventListener implements Listener {
    @EventHandler
    /**
     * Check whether players are allowed to log in.
     */
    public void onPreLoginEvent(PreLoginEvent e) {
        var user = UsernameValidator.getUser(e.getConnection().getName());

        if (user == null) {
            return;
        }

        BetterWhitelistBungee.getInstance().getLogger().info("Checking that UUID '" + user.id +"' is whitelisted...");

       if (SQLConnection.getDiscordIDFromMinecraft(user.id) == null) {
           e.setCancelled(true);
           e.setCancelReason(new TextComponent(ChatColor.RED + "You are not whitelisted on this network!"));
       }
    }
}
