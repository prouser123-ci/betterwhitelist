package com.dumbdogdiner.betterwhitelist_bungee.discord.lib;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Wrapper class for objects used commonly in Commands.
 */
public class Context {
    public Member executor;
    public Message message;
    public MessageReceivedEvent _raw;

    public Context(MessageReceivedEvent e) {
        executor = e.getMember();
        message = e.getMessage();
        _raw = e;
    }
}
