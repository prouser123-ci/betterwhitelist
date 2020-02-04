package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WhitelistCommand extends Command {
    private static String name = "whitelist";

    @Override
    public boolean run(MessageReceivedEvent ctx, String... args) {
        return true;
    }
}
