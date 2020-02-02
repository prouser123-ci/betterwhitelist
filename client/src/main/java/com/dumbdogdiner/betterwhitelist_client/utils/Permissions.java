package com.dumbdogdiner.betterwhitelist_client.utils;

import com.dumbdogdiner.betterwhitelist_client.BetterWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Permissions {
    /**
     * Check if the specified sender has the specified permission.
     */
    public static boolean checkPermission(CommandSender sender, String subKey) {
        if (!sender.hasPermission("betterwhitelist.command." + subKey)) {
            sender.sendMessage(ChatColor.RED + "You don't have the required permissions to do this!");
            return false;
        }
        return true;
    }
    public static boolean checkPermission(CommandSender sender) {
        if (!sender.hasPermission("betterwhitelist.command")) {
            sender.sendMessage(ChatColor.RED + "You don't have the required permissions to do this!");
            return false;
        }
        return true;
    }
}
