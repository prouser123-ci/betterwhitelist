package com.dumbdogdiner.betterwhitelist_bungee.bungee.commands;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class UnwhitelistCommand extends Command {

    public UnwhitelistCommand() {
        super("unwhitelist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: <username>"));
            return;
        }

        var user = UsernameValidator.getUser(args[0]);

        if (user == null || user.id == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user of name '" + args[0] +"'."));
            return;
        }

        if (SQLConnection.getDiscordIDFromMinecraft(user.id) == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player '" + args[0] +"' is not whitelisted."));
            return;
        }

        if (SQLConnection.removeEntryUsingUuid(user.id)) {
            sender.sendMessage(new TextComponent(ChatColor.AQUA + "Removed user " + user.name +" ('" + user.id +"') from the whitelist."));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to unmap user - SQL update failed."));
        }
    }
}
