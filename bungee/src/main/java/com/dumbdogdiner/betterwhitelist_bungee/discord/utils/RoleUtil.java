package com.dumbdogdiner.betterwhitelist_bungee.discord.utils;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleUtil {
    /**
     * Check if a requiredRole is needed.
     * @param e
     * @return
     */
    public static boolean checkRequiredRole(MessageReceivedEvent e) {
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
    public static void addGrantedRole(MessageReceivedEvent e) {
        if (PluginConfig.getConfig().getBoolean("discord.roles.grantedRole.enabled")) {
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
                    e.getChannel().sendMessage(":inbox_tray: You were also granted the **" + role.getName() + "** role!").queue();
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
    public static void sendRoleAddErrorMessage(MessageReceivedEvent e) {
        e.getChannel()
                .sendMessage(":warning: You are also supposed to be granted a role," +
                        "but something went wrong while I was trying to add it to you. Please contact an admin so they can add it for you!")
                .queue();
    }

    /**
     * Remove the granted role from a member.
     * @param e
     */
    public static void removeGrantedRole(MessageReceivedEvent e) {
        if (PluginConfig.getConfig().getBoolean("discord.roles.grantedRole.enabled")) {
            try {
                var roleId = PluginConfig.getConfig().getString("discord.roles.grantedRole.roleId");
                var role = e.getGuild().getRoleById(roleId);
                var member = e.getMember();

                // If the role doesn't exist, inform the user that something went wrong.
                if (role == null || member == null) {
                    WhitelistBot.getLogger().warning("[discord] Unable to retrieve grantedRole '" + roleId + "'.");
                    return;
                }

                // Don't try and remove the role if they don't already have it.
                if (!member.getRoles().contains(role)) {
                    return;
                }

                // Add role to user.
                e.getGuild().removeRoleFromMember(member, role).queue();

            } catch(Exception err) {
                err.printStackTrace();
            }
        }
    }
}
