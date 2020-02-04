package com.dumbdogdiner.betterwhitelist_bungee.discord.commands;

import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Command;
import com.dumbdogdiner.betterwhitelist_bungee.utils.UsernameValidator;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        this.name = "minecraft";
    }

    @Override
    public boolean run(MessageReceivedEvent ctx, String... args) {
        if (args.length == 0 || args[0] == null) {
            ctx.getChannel().sendMessage(
                ":x: **Whoops!** You didn't specify your MC username. Make sure you run the command in the format `-minecraft <username>`."
            ).queue();
           return true;
        }

        ctx.getChannel().sendTyping().queue();
        var user = UsernameValidator.getUser(args[0]);

        if (user == null) {
            ctx.getChannel().sendMessage(":x: **Failed to verify!** Perhaps try again?");
        }

        ctx.getChannel()
            .sendMessage(":white_check_mark: Whitelisted user **" + user.name + "**! (`" + user.id + "`)")
            .queue();

        return true;
    }
}
