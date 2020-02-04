package com.dumbdogdiner.betterwhitelist_bungee.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Listen for player joins and check them against the SQL database.
 */
public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PostLoginEvent e) {
       var uuid = e.getPlayer().getUniqueId().toString();

       if (SQLConnection.getDiscordIDFromMinecraft(uuid.toString()) == null) {
           e.getPlayer().disconnect(new TextComponent(ChatColor.RED + "You are not whitelisted on this network!"));
       }
    }
}
