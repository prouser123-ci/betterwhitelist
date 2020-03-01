package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.BetterWhitelistBungee;
import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.stream.Collectors;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.name = "help";
        this.description = "Shows the list of available bot commands.";
    }

    @Override
    public void run(MessageReceivedEvent e, String... args) {
        e.getChannel().sendMessage(String.format(
                "**Aarrff!!** BetterWhitelist `v%s`\n\nAvailable Commands:\n - %s",
                BetterWhitelistBungee.getInstance().getDescription().getVersion(),
                WhitelistBot.getCommands().values().stream().map(this::formatCommandInfo).collect(Collectors.joining("\n - "))
        )).queue();
    }

    private String formatCommandInfo(Command command) {
        if (command.getSyntax() != null) {
            return String.format("`%s %s` - %s", command.getName(), command.getSyntax(), command.getDescription());
        } else {
            return String.format("`%s` - %s", command.getName(), command.getDescription());
        }
    }
}
