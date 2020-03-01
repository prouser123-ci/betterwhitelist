package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.discord.utils.RoleUtil;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        this.name = "whitelist";
        this.description = "Add yourself to the whitelist of the Minecraft server.";
        this.syntax = "<username>";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        if (!PluginConfig.getConfig().getBoolean("discord.enableSelfWhitelisting")) {
            e.getChannel()
                .sendMessage(":x: **Self-whitelisting has been disabled.**")
                .queue();
            return;
        }

        if (!RoleUtil.checkRequiredRole(e)) {
            e.getChannel().sendMessage(
                ":x: **Whoops!** You don't have the role needed to be able to self-whitelist."
            ).queue();
            return;
        }

        if (args.length == 0 || args[0] == null) {
            e.getChannel().sendMessage(
                ":x: **Whoops!** You didn't specify your MC username. Make sure you run the command in the format `"+ PluginConfig.getPrefix() + "whitelist <username>`."
            ).queue();
           return;
        }

        e.getChannel().sendTyping().queue();

        if (
            SQLConnection.getUuidFromDiscordId(e.getAuthor().getId()) != null &&
            PluginConfig.getConfig().getBoolean("discord.oneAccountPerUser")
        ) {
            e.getChannel()
                .sendMessage(":x: **Failed to verify!** You already have a Minecraft account whitelisted - you can unwhitelist it by running `"+ PluginConfig.getPrefix() +"unwhitelist`.")
                .queue();
            return;
        }

        var user = UsernameValidator.getUser(args[0]);

        if (user == null || user.id == null) {
            e.getChannel()
                .sendMessage(":x: **Failed to verify!** Invalid Username - did you make a typo somewhere?")
                .queue();
            return;
        }

        // Add user to SQL.
        if (!SQLConnection.addEntry(e.getAuthor().getId(), user.id)) {
            e.getChannel()
                    .sendMessage(":x: **Failed to add you to whitelist!** Please contact a dev so they can manually add you. (`SQL_ERROR`)")
                    .queue();

            return;
        }

        e.getChannel()
            .sendMessage(":white_check_mark: Whitelisted user **" + user.name + "**! (`" + user.id + "`)")
            .queue(message -> RoleUtil.addGrantedRole(e));
    }

}
