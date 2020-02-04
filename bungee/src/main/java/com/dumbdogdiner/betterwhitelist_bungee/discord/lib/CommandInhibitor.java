package com.dumbdogdiner.betterwhitelist_bungee.discord.lib;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * An object that prevents command execution if the result of its `inhibit` method is false.
 */
public interface CommandInhibitor {
    boolean inhibit(Command command, MessageReceivedEvent context);
}
