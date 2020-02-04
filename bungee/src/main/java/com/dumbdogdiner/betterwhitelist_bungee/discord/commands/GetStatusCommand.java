package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GetStatusCommand extends Command {

    public GetStatusCommand() {
        this.name = "status";
    }

    @Override
    public boolean run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendMessage(String.format(
            "**Meep!! ^w^**\nPing: `%dms`",
            Math.round(WhitelistBot.getJda().getGatewayPing()
        ))).queue();

        return true;
    }
}
