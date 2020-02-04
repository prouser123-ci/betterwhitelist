package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.utils.SQLConnection;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Unwhitelist extends Command {

    public Unwhitelist() {
        this.name = "unwhitelist";
        this.description = "Remove yourself from the whitelist of the Minecraft server.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        // TODO: Check why the user lookup failed.
        e.getChannel().sendTyping().queue();

        if (SQLConnection.removeEntry(e.getMessageId())) {
          e.getChannel()
              .sendMessage(":white_check_mark: **You were removed from the whitelist!**")
              .queue();

        } else {
            e.getChannel()
                .sendMessage(":x: **Could not remove you from the whitelist!** Perhaps you aren't on it to begin with?")
                .queue();
        }
    }
}
