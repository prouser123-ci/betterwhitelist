package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.MessageFormat;

public class UnwhitelistCommand extends Command {

    public UnwhitelistCommand() {
        this.name = "unwhitelist";
        this.description = "Remove yourself from the whitelist of the Minecraft server.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendTyping().queue();

        if (SQLConnection.removeEntry(e.getAuthor().getId())) {
            removeGrantedRole(e);
            e.getChannel()
                .sendMessage(":white_check_mark: **You were removed from the whitelist!**")
                .queue();
        } else {
            e.getChannel()
                .sendMessage(":x: **Could not remove you from the whitelist!** Perhaps you aren't on it to begin with?")
                .queue();
        }
    }

    /**
     * Remove the granted role from a member.
     * TODO: Minimize code dupe between here and the whitelist command.
     * @param e
     */
    private void removeGrantedRole(MessageReceivedEvent e) {
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
