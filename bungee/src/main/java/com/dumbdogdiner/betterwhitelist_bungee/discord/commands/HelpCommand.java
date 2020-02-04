package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.name = "help";
    }

    @Override
    public boolean run(MessageReceivedEvent e, String... args) {
        return true;
    }
}
