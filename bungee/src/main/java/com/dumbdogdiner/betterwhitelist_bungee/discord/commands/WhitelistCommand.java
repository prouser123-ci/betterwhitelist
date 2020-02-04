package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        this.name = "minecraft";
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

        if (!checkRequiredRole(e)) {
            e.getChannel().sendMessage(
                ":x: **Whoops!** You don't have the role needed to be able to self-whitelist."
            ).queue();
            return;
        }

        if (args.length == 0 || args[0] == null) {
            e.getChannel().sendMessage(
                ":x: **Whoops!** You didn't specify your MC username. Make sure you run the command in the format `-minecraft <username>`."
            ).queue();
           return;
        }

        // TODO: Check why the user lookup failed.
        e.getChannel().sendTyping().queue();

        if (
            SQLConnection.getUuidFromDiscordId(e.getAuthor().getId()) != null &&
            PluginConfig.getConfig().getBoolean("oneAccountPerUser")
        ) {
            e.getChannel()
                .sendMessage(":x: **Failed to verify!** You already have a Minecraft account whitelisted - you can unwhitelist it by running `-unwhitelist`.")
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
            .queue();

        addGrantedRole(e);
    }

    /**
     * Check if a requiredRole is needed.
     * @param e
     * @return
     */
    private boolean checkRequiredRole(MessageReceivedEvent e) {
        if (!PluginConfig.getConfig().getBoolean("discord.roles.requiredRole.enabled")) {
            return true;
        }

        var roleId = PluginConfig.getConfig().getString("discord.roles.requiredRole.roleId");
        var role = e.getGuild().getRoleById(roleId);
        var member = e.getMember();

        // Prevent whitelisting just in case.
        if (role == null || member == null) {
            WhitelistBot.getLogger().warning("[discord] Unable to retrieve requiredRole '" + roleId + "'.");
            return false;
        }

        return member.getRoles().contains(role);
    }

    /**
     * Add the grantedRole to users if there is one.
     * @param e
     */
    private void addGrantedRole(MessageReceivedEvent e) {
        if (!PluginConfig.getConfig().getBoolean("discord.roles.grantedRole.enabled")) {
            try {
                var roleId = PluginConfig.getConfig().getString("discord.roles.grantedRole.roleId");
                var role = e.getGuild().getRoleById(roleId);
                var member = e.getMember();

                // If the role doesn't exist, inform the user that something went wrong.
                if (role == null || member == null) {
                    WhitelistBot.getLogger().warning("[discord] Unable to retrieve grantedRole '" + roleId + "'.");
                    sendRoleAddErrorMessage(e);
                    return;
                }

                // Don't try and add the role if they already have it.
                if (member.getRoles().contains(role)) {
                    return;
                }

                // Add role to user.
                e.getGuild().addRoleToMember(member, role).queue();

                // Tell users they have been granted the role.
                if (!PluginConfig.getConfig().getBoolean("discord.roles.grantedRole.silent")) {
                    e.getChannel().sendMessage(":inbox_tray: You were also granted the **" + role.getName() + "** role!");
                }
            } catch(Exception err) {
                err.printStackTrace();
                sendRoleAddErrorMessage(e);
            }
        }
    }

    /**
     * Send an error message telling users their granted role couldn't be added to them.
     * @param e
     */
    private void sendRoleAddErrorMessage(MessageReceivedEvent e) {
        e.getChannel()
            .sendMessage(":warning: You are also supposed to be granted a role," +
                "but something went wrong while I was trying to add it to you. Please contact an admin so they can add it for you!")
            .queue();
    }
}
