package com.dumbdogdiner.betterwhitelist_bungee.discord.lib;

/**
 * An object that prevents command execution if the result of its `inhibit` method is false.
 */
public interface CommandInhibitor {
    boolean inhibit(Command command, Context context);
}