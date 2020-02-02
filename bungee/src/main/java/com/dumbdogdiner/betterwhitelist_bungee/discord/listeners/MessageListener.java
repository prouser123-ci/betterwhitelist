package com.dumbdogdiner.betterwhitelist_bungee.discord.listeners;

import com.dumbdogdiner.betterwhitelist_bungee.discord.WhitelistBot;
import com.dumbdogdiner.betterwhitelist_bungee.discord.lib.Context;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Listens for new messages.
 */
public class MessageListener extends ListenerAdapter {

    public WhitelistBot bot;

    public MessageListener(WhitelistBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        String rawContent = e.getMessage().getContentRaw();
        String prefix = bot.configManager.getConfig().getString("discord.prefix");

        if (e.getAuthor().isBot() || e.getChannelType() == ChannelType.PRIVATE || !rawContent.startsWith(prefix)) {
            return;
        }

        if (e.getMessage().isMentioned(bot.jda.getSelfUser())) {
            e.getChannel().sendMessage("Hai!! ^w^ My prefix is `" + prefix + "`.").queue();
            return;
        }

        String content = rawContent.substring(prefix.length());
        List<String> args = Arrays.asList(content.split(" "));
        String commandName = args.remove(0);

        if (bot.commands.containsKey(commandName)) {
            bot.commands.get(commandName).execute(new Context(e), (String[]) args.toArray());
        }
    }
}
