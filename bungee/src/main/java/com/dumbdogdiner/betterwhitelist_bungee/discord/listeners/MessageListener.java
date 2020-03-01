package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.utils.PluginConfig;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

/**
 * Listens for new messages.
 */
public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        var rawContent = e.getMessage().getContentRaw();
        var prefix = PluginConfig.getConfig().getString("discord.prefix");
        var guildId = PluginConfig.getConfig().getString("discord.guildId");

        if (e.getAuthor().isBot() || e.getChannelType() == ChannelType.PRIVATE  || !e.getGuild().getId().equals(guildId)) {
            return;
        }

        if (e.getMessage().isMentioned(WhitelistBot.getJda().getSelfUser())) {
            e.getChannel().sendMessage("**Hai!! ^w^** My prefix is `" + prefix + "`.").queue();
            return;
        }

        if (
            !rawContent.startsWith(prefix)
        ) {
            return;
        }

        var content = rawContent.substring(prefix.length());
        var args = content.split(" ");
        var commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        if (!WhitelistBot.getCommands().containsKey(commandName)) {
            e.getChannel()
                    .sendMessage(":x: **Oops!** Unknown command `" + commandName + "` - do `" + prefix + "help` for a list of commands.")
                    .queue();
            return;
        }

        WhitelistBot.getLogger().info(String.format("[discord] %s (%s) => %s", e.getAuthor().getAsTag(),e.getAuthor().getId(), commandName));

        try {
            WhitelistBot.getCommands().get(commandName).execute(e, args);
        } catch(Exception err) {
            WhitelistBot.getLogger().severe("Error in command '" + commandName + "':");
            err.printStackTrace();

            e.getChannel().sendMessage(String.format(
                ":x: **Whoops!** Internal error - please ask a dev to take a look. (`%s`)",
                err.getClass().getCanonicalName()
            )).queue();
       }
    }
}
