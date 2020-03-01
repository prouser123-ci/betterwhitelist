package com.dumbdogdiner.betterwhitelist_bungee.bungee.commands;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        super("whitelist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid arguments - syntax: <username> <discord_id>"));
            return;
        }

        var user = UsernameValidator.getUser(args[0]);
        var discordId = args.length > 1 ? args[1] : "none";

        if (user == null || user.id == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Unable to find a user with name '" + args[0] +"'."));
            return;
        }

        if (SQLConnection.getDiscordIDFromMinecraft(user.id) != null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player " + args[0] +" is already whitelisted. Run 'unwhitelist <player_name>' to remove them."));
            return;
        }

        if (SQLConnection.addEntry(discordId, user.id)) {
            sender.sendMessage(new TextComponent(ChatColor.AQUA + "Mapped user " + user.name +" to Discord ID '" + discordId +"'."));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Failed to map user - SQL update failed."));
        }
    }
}
