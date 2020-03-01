package com.dumbdogdiner.betterwhitelist_bungee.bungee.commands;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WhoisCommand extends Command {

    public WhoisCommand() {
        super("whois");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0] == null || args.length > 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Command Syntax: whois <uuid/discord/name>"));
        }

        var target = args[0];

        String discordId = null;
        String playerUuid;

        ProxiedPlayer player = null;

        if (
                target.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}") ||
                target.matches("[a-zA-Z0-9_]{1,16}")
        ) {
            player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(target);
        } else {
            playerUuid = SQLConnection.getUuidFromDiscordId(target);
            if (playerUuid != null) {
                player = BetterWhitelistBungee.getInstance().getProxy().getPlayer(playerUuid);
                discordId = SQLConnection.getDiscordIDFromMinecraft(playerUuid);
            }
        }

        if (player == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Could not find player."));
            return;
        }

        // Feel like this could be shortened.
        sender.sendMessage(new TextComponent(ChatColor.BLUE + "Information for user: " + ChatColor.WHITE + player.getName()));
        sender.sendMessage(new TextComponent(ChatColor.BLUE + "UUID: " + ChatColor.WHITE + player.getUniqueId().toString()));
        sender.sendMessage(new TextComponent(ChatColor.BLUE + "Discord ID: " + ChatColor.WHITE + discordId));
        sender.sendMessage(new TextComponent(ChatColor.BLUE + "Whitelisted: " + ChatColor.WHITE + (discordId == null ? "Yes" : "No")));
    }
}
