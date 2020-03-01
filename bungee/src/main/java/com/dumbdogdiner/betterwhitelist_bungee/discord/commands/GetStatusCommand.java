package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GetStatusCommand extends Command {

    public GetStatusCommand() {
        this.name = "status";
        this.description = "Gets the bot's status.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendMessage(String.format(
            "**Meep!! ^w^**\n> Ping: `%dms`",
            Math.round(WhitelistBot.getJda().getGatewayPing()
        ))).queue();
    }
}
